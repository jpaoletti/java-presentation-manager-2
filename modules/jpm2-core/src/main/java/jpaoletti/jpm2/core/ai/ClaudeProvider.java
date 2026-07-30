package jpaoletti.jpm2.core.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import jpaoletti.jpm2.core.log.DebugLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Reference {@link AIProviderImplementation} for Anthropic's Claude, over the Messages API
 * ({@code POST /v1/messages}). Uses the JDK HTTP client ({@code java.net.http}) and {@code org.json} — no
 * extra dependency added to jpm2-core. This mirrors how trekker's IA subsystem talks to its provider today,
 * and keeps the core lean for apps on older stacks; it can be re-based on the official {@code anthropic-java}
 * SDK later without changing the {@link AIProviderImplementation} contract.
 *
 * <p>Fase 0 scope: synchronous CHAT + STRUCTURED_OUTPUT. Streaming, tools, vision and embeddings are later
 * phases (see PLAN_MIGRACION_AI.md).
 *
 * <p>Wire notes (current Messages API): the system prompt is a top-level {@code system} field, not a message;
 * structured output uses {@code output_config.format} (the old {@code output_format} is deprecated);
 * sampling parameters ({@code temperature}/{@code top_p}/{@code top_k}) are NOT sent because current Opus/Sonnet
 * models reject them with HTTP 400; the {@code thinking} parameter is omitted (adaptive thinking is the model
 * default). A safety-policy decline comes back as HTTP 200 with {@code stop_reason == "refusal"} and is
 * surfaced via {@link AICompletion#isRefusal()}, not as an exception.
 */
public class ClaudeProvider implements AIProviderImplementation {

    public static final String CODE = "claude";

    private static final String DEBUG_CHANNEL = "ai-connector";
    private static final String DEFAULT_BASE_URL = "https://api.anthropic.com";
    private static final String DEFAULT_MODEL = "claude-opus-5";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final long DEFAULT_TIMEOUT_MS = 60_000L;

    private final HttpClient httpClient;

    public ClaudeProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public boolean supports(AICapability capability) {
        return capability == AICapability.CHAT || capability == AICapability.STRUCTURED_OUTPUT;
    }

    @Override
    public AICompletion complete(AIConnectorConfig config, AIRequest request) throws AIException {
        if (config == null || config.getApiKey() == null || config.getApiKey().isBlank()) {
            throw new AIException("Missing Claude API key in the connector configuration");
        }
        final String baseUrl = stripTrailingSlash(firstNonBlank(config.getBaseUrl(), DEFAULT_BASE_URL));
        final JSONObject body = buildBody(config, request);
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/messages"))
                .timeout(Duration.ofMillis(resolveTimeoutMs(config)))
                .header("x-api-key", config.getApiKey())
                .header("anthropic-version", ANTHROPIC_VERSION)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        DebugLog.debug(DEBUG_CHANNEL, 2, () -> "Claude POST " + baseUrl + "/v1/messages model=" + body.optString("model", "?")
                + " maxTokens=" + body.opt("max_tokens") + " hasSchema=" + body.has("output_config"));
        DebugLog.debug(DEBUG_CHANNEL, 3, () -> "Claude request body: " + body.toString());
        try {
            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "Claude HTTP " + response.statusCode());
            DebugLog.debug(DEBUG_CHANNEL, 3, () -> "Claude response body: " + response.body());
            return parseResponse(response.statusCode(), response.body(), request);
        } catch (AIException e) {
            throw e;
        } catch (Exception e) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "Claude transport error: " + e.getMessage());
            throw new AIException("Claude request failed: " + e.getMessage(), e);
        }
    }

    private JSONObject buildBody(AIConnectorConfig config, AIRequest request) throws AIException {
        final JSONObject body = new JSONObject();
        body.put("model", firstNonBlank(request.getModel(), config.getDefaultModel(), DEFAULT_MODEL));
        body.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : DEFAULT_MAX_TOKENS);

        final String system = collectSystem(request);
        if (system != null && !system.isBlank()) {
            body.put("system", system);
        }

        final JSONArray messages = new JSONArray();
        for (AIMessage message : request.getMessages()) {
            if (message.getRole() == AIRole.SYSTEM) {
                continue; // folded into the top-level system field
            }
            messages.put(new JSONObject()
                    .put("role", message.getRole() == AIRole.ASSISTANT ? "assistant" : "user")
                    .put("content", message.getContent()));
        }
        body.put("messages", messages);

        if (request.getJsonSchema() != null && !request.getJsonSchema().isBlank()) {
            try {
                final JSONObject schema = new JSONObject(request.getJsonSchema());
                body.put("output_config", new JSONObject()
                        .put("format", new JSONObject()
                                .put("type", "json_schema")
                                .put("schema", schema)));
            } catch (JSONException e) {
                throw new AIException("Invalid JSON schema for structured output", e);
            }
        }

        // Provider-specific passthrough (thin-core policy). Applied last so advanced callers can override
        // defaults; note that sending sampling params here would be rejected by current Claude models.
        for (Map.Entry<String, Object> entry : request.getExtras().entrySet()) {
            body.put(entry.getKey(), entry.getValue());
        }
        return body;
    }

    private AICompletion parseResponse(int status, String body, AIRequest request) throws AIException {
        if (status != 200) {
            throw new AIException("Claude HTTP " + status + ": " + summarize(body));
        }
        final JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (JSONException e) {
            throw new AIException("Unparseable Claude response: " + summarize(body), e);
        }

        final String stopReason = json.optString("stop_reason", null);
        final boolean refusal = "refusal".equals(stopReason);
        final String model = json.optString("model", null);

        final StringBuilder text = new StringBuilder();
        final JSONArray content = json.optJSONArray("content");
        if (content != null) {
            for (int i = 0; i < content.length(); i++) {
                final JSONObject block = content.optJSONObject(i);
                if (block != null && "text".equals(block.optString("type"))) {
                    text.append(block.optString("text", ""));
                }
            }
        }

        int promptTokens = 0;
        int completionTokens = 0;
        final JSONObject usage = json.optJSONObject("usage");
        if (usage != null) {
            promptTokens = usage.optInt("input_tokens", 0);
            completionTokens = usage.optInt("output_tokens", 0);
        }

        final boolean structured = request.getJsonSchema() != null && !request.getJsonSchema().isBlank();
        final String structuredJson = (structured && !refusal) ? text.toString() : null;

        return new AICompletion(text.toString(), structuredJson, model,
                new AIUsage(promptTokens, completionTokens), stopReason, refusal, body);
    }

    private String collectSystem(AIRequest request) {
        final StringBuilder sb = new StringBuilder();
        if (request.getSystem() != null && !request.getSystem().isBlank()) {
            sb.append(request.getSystem());
        }
        for (AIMessage message : request.getMessages()) {
            if (message.getRole() == AIRole.SYSTEM && message.getContent() != null) {
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
                sb.append(message.getContent());
            }
        }
        return sb.toString();
    }

    private long resolveTimeoutMs(AIConnectorConfig config) {
        final String raw = config.getParameter("timeout-ms", null);
        if (raw != null) {
            try {
                return Long.parseLong(raw.trim());
            } catch (NumberFormatException ignore) {
                // fall through to the default
            }
        }
        return DEFAULT_TIMEOUT_MS;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static String stripTrailingSlash(String url) {
        return (url != null && url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
    }

    private static String summarize(String body) {
        if (body == null) {
            return "(no body)";
        }
        final String trimmed = body.strip();
        return trimmed.length() > 500 ? trimmed.substring(0, 500) + "…" : trimmed;
    }
}
