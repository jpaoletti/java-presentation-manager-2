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
 * Reference {@link AIProviderImplementation} for OpenAI, over the Chat Completions API
 * ({@code POST /chat/completions}). Uses the JDK HTTP client + {@code org.json} (no extra dependency).
 * This is the generalization of trekker's existing OpenAI integration into the neutral contract.
 *
 * <p>Fase 0 scope: synchronous CHAT + STRUCTURED_OUTPUT (via {@code response_format: json_schema}).
 * The model is taken from the request or the connector's default; there is no hardcoded fallback model.
 */
public class OpenAIProvider implements AIProviderImplementation {

    public static final String CODE = "openai";

    private static final String DEBUG_CHANNEL = "ai-connector";
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final long DEFAULT_TIMEOUT_MS = 60_000L;

    private final HttpClient httpClient;

    public OpenAIProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    public AICompletion complete(AIConnectorConfig config, AIRequest request) throws AIException {
        if (config == null || config.getApiKey() == null || config.getApiKey().isBlank()) {
            throw new AIException("Missing OpenAI API key in the connector configuration");
        }
        final String model = firstNonBlank(request.getModel(), config.getDefaultModel());
        if (model == null) {
            throw new AIException("No model configured for the OpenAI connector");
        }
        final String baseUrl = stripTrailingSlash(firstNonBlank(config.getBaseUrl(), DEFAULT_BASE_URL));
        final JSONObject body = buildBody(config, request, model);
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .timeout(Duration.ofMillis(resolveTimeoutMs(config)))
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        DebugLog.debug(DEBUG_CHANNEL, 2, () -> "OpenAI POST " + baseUrl + "/chat/completions model=" + model
                + " maxTokens=" + body.opt("max_tokens") + " hasSchema=" + body.has("response_format"));
        DebugLog.debug(DEBUG_CHANNEL, 3, () -> "OpenAI request body: " + body.toString());
        try {
            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "OpenAI HTTP " + response.statusCode());
            DebugLog.debug(DEBUG_CHANNEL, 3, () -> "OpenAI response body: " + response.body());
            return parseResponse(response.statusCode(), response.body(), request);
        } catch (AIException e) {
            throw e;
        } catch (Exception e) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "OpenAI transport error: " + e.getMessage());
            throw new AIException("OpenAI request failed: " + e.getMessage(), e);
        }
    }

    private JSONObject buildBody(AIConnectorConfig config, AIRequest request, String model) throws AIException {
        final JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : DEFAULT_MAX_TOKENS);
        if (request.getTemperature() != null) {
            body.put("temperature", request.getTemperature().doubleValue());
        }

        // OpenAI keeps the system prompt as a message with role "system".
        final JSONArray messages = new JSONArray();
        if (request.getSystem() != null && !request.getSystem().isBlank()) {
            messages.put(new JSONObject().put("role", "system").put("content", request.getSystem()));
        }
        for (AIMessage message : request.getMessages()) {
            messages.put(new JSONObject()
                    .put("role", openAiRole(message.getRole()))
                    .put("content", message.getContent()));
        }
        body.put("messages", messages);

        if (request.getJsonSchema() != null && !request.getJsonSchema().isBlank()) {
            try {
                final JSONObject schema = new JSONObject(request.getJsonSchema());
                body.put("response_format", new JSONObject()
                        .put("type", "json_schema")
                        .put("json_schema", new JSONObject()
                                .put("name", "response")
                                .put("strict", true)
                                .put("schema", schema)));
            } catch (JSONException e) {
                throw new AIException("Invalid JSON schema for structured output", e);
            }
        }

        for (Map.Entry<String, Object> entry : request.getExtras().entrySet()) {
            body.put(entry.getKey(), entry.getValue());
        }
        return body;
    }

    private AICompletion parseResponse(int status, String body, AIRequest request) throws AIException {
        if (status != 200) {
            throw new AIException("OpenAI HTTP " + status + ": " + summarize(body));
        }
        final JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (JSONException e) {
            throw new AIException("Unparseable OpenAI response: " + summarize(body), e);
        }

        String text = "";
        String finishReason = null;
        final JSONArray choices = json.optJSONArray("choices");
        if (choices != null && choices.length() > 0) {
            final JSONObject choice = choices.optJSONObject(0);
            if (choice != null) {
                finishReason = choice.optString("finish_reason", null);
                final JSONObject message = choice.optJSONObject("message");
                if (message != null) {
                    text = message.optString("content", "");
                }
            }
        }

        int promptTokens = 0;
        int completionTokens = 0;
        final JSONObject usage = json.optJSONObject("usage");
        if (usage != null) {
            promptTokens = usage.optInt("prompt_tokens", 0);
            completionTokens = usage.optInt("completion_tokens", 0);
        }

        final boolean structured = request.getJsonSchema() != null && !request.getJsonSchema().isBlank();
        final String structuredJson = structured ? text : null;

        return new AICompletion(text, structuredJson, json.optString("model", null),
                new AIUsage(promptTokens, completionTokens), finishReason, false, body);
    }

    private static String openAiRole(AIRole role) {
        switch (role) {
            case SYSTEM:
                return "system";
            case ASSISTANT:
                return "assistant";
            default:
                return "user";
        }
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
