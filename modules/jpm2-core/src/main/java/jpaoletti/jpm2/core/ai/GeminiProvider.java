package jpaoletti.jpm2.core.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import jpaoletti.jpm2.core.log.DebugLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Reference {@link AIProviderImplementation} for Google Gemini, over the Generative Language API
 * ({@code POST /v1beta/models/{model}:generateContent}). Uses the JDK HTTP client + {@code org.json}.
 *
 * <p>Fase 0 scope: synchronous CHAT + STRUCTURED_OUTPUT (via {@code responseMimeType} + {@code responseSchema}).
 * The model is taken from the request or the connector's default; there is no hardcoded fallback model.
 * Gemini roles: USER maps to {@code "user"}, ASSISTANT to {@code "model"}, and the system prompt goes into
 * {@code systemInstruction}.
 */
public class GeminiProvider implements AIProviderImplementation {

    public static final String CODE = "gemini";

    private static final String DEBUG_CHANNEL = "ai-connector";
    private static final String DEFAULT_BASE_URL = "https://generativelanguage.googleapis.com";
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final long DEFAULT_TIMEOUT_MS = 60_000L;

    private final HttpClient httpClient;

    public GeminiProvider() {
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
            throw new AIException("Missing Gemini API key in the connector configuration");
        }
        final String model = firstNonBlank(request.getModel(), config.getDefaultModel());
        if (model == null) {
            throw new AIException("No model configured for the Gemini connector");
        }
        final String baseUrl = stripTrailingSlash(firstNonBlank(config.getBaseUrl(), DEFAULT_BASE_URL));
        final JSONObject body = buildBody(request);
        final String url = baseUrl + "/v1beta/models/" + model + ":generateContent?key=" + config.getApiKey();
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(resolveTimeoutMs(config)))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        DebugLog.debug(DEBUG_CHANNEL, 2, () -> "Gemini POST " + baseUrl + "/v1beta/models/" + model
                + ":generateContent (key redacted) hasSchema="
                + (request.getJsonSchema() != null && !request.getJsonSchema().isBlank()));
        DebugLog.debug(DEBUG_CHANNEL, 3, () -> "Gemini request body: " + body.toString());
        try {
            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "Gemini HTTP " + response.statusCode());
            DebugLog.debug(DEBUG_CHANNEL, 3, () -> "Gemini response body: " + response.body());
            return parseResponse(response.statusCode(), response.body(), request, model);
        } catch (AIException e) {
            throw e;
        } catch (Exception e) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "Gemini transport error: " + e.getMessage());
            throw new AIException("Gemini request failed: " + e.getMessage(), e);
        }
    }

    private JSONObject buildBody(AIRequest request) throws AIException {
        final JSONObject body = new JSONObject();

        final String system = collectSystem(request);
        if (system != null && !system.isBlank()) {
            body.put("systemInstruction", new JSONObject()
                    .put("parts", new JSONArray().put(new JSONObject().put("text", system))));
        }

        final JSONArray contents = new JSONArray();
        for (AIMessage message : request.getMessages()) {
            if (message.getRole() == AIRole.SYSTEM) {
                continue; // folded into systemInstruction
            }
            contents.put(new JSONObject()
                    .put("role", message.getRole() == AIRole.ASSISTANT ? "model" : "user")
                    .put("parts", new JSONArray().put(new JSONObject().put("text", message.getContent()))));
        }
        body.put("contents", contents);

        final JSONObject generationConfig = new JSONObject();
        generationConfig.put("maxOutputTokens", request.getMaxTokens() != null ? request.getMaxTokens() : DEFAULT_MAX_TOKENS);
        if (request.getTemperature() != null) {
            generationConfig.put("temperature", request.getTemperature().doubleValue());
        }
        if (request.getJsonSchema() != null && !request.getJsonSchema().isBlank()) {
            try {
                generationConfig.put("responseMimeType", "application/json");
                generationConfig.put("responseSchema", new JSONObject(request.getJsonSchema()));
            } catch (JSONException e) {
                throw new AIException("Invalid JSON schema for structured output", e);
            }
        }
        body.put("generationConfig", generationConfig);
        return body;
    }

    private AICompletion parseResponse(int status, String body, AIRequest request, String model) throws AIException {
        if (status != 200) {
            throw new AIException("Gemini HTTP " + status + ": " + summarize(body));
        }
        final JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (JSONException e) {
            throw new AIException("Unparseable Gemini response: " + summarize(body), e);
        }

        String text = "";
        String finishReason = null;
        final JSONArray candidates = json.optJSONArray("candidates");
        if (candidates != null && candidates.length() > 0) {
            final JSONObject candidate = candidates.optJSONObject(0);
            if (candidate != null) {
                finishReason = candidate.optString("finishReason", null);
                final JSONObject content = candidate.optJSONObject("content");
                if (content != null) {
                    final JSONArray parts = content.optJSONArray("parts");
                    if (parts != null) {
                        final StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < parts.length(); i++) {
                            final JSONObject part = parts.optJSONObject(i);
                            if (part != null) {
                                sb.append(part.optString("text", ""));
                            }
                        }
                        text = sb.toString();
                    }
                }
            }
        }

        int promptTokens = 0;
        int completionTokens = 0;
        final JSONObject usage = json.optJSONObject("usageMetadata");
        if (usage != null) {
            promptTokens = usage.optInt("promptTokenCount", 0);
            completionTokens = usage.optInt("candidatesTokenCount", 0);
        }

        final boolean refusal = "SAFETY".equals(finishReason) || "PROHIBITED_CONTENT".equals(finishReason)
                || "BLOCKLIST".equals(finishReason);
        final boolean structured = request.getJsonSchema() != null && !request.getJsonSchema().isBlank();
        final String structuredJson = (structured && !refusal) ? text : null;

        return new AICompletion(text, structuredJson, model,
                new AIUsage(promptTokens, completionTokens), finishReason, refusal, body);
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
