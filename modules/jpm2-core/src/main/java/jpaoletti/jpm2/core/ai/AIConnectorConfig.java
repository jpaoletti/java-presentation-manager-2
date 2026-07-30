package jpaoletti.jpm2.core.ai;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Runtime configuration a connector hands to an {@link AIProviderImplementation}: the resolved (decrypted)
 * credentials and provider settings. This decouples the provider implementations from the persistent
 * {@code AIConnector} entity (planned) — a provider only ever sees this value object, never the entity.
 */
public class AIConnectorConfig {

    private final String apiKey;
    private final String baseUrl;
    private final String defaultModel;
    private final Map<String, String> parameters;

    public AIConnectorConfig(String apiKey, String baseUrl, String defaultModel, Map<String, String> parameters) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.defaultModel = defaultModel;
        this.parameters = (parameters != null)
                ? Collections.unmodifiableMap(new LinkedHashMap<>(parameters))
                : Collections.emptyMap();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /** Reads an extra provider parameter, falling back to {@code defaultValue} when absent. */
    public String getParameter(String key, String defaultValue) {
        final String value = parameters.get(key);
        return (value != null) ? value : defaultValue;
    }
}
