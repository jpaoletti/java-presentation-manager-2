package jpaoletti.jpm2.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.ai.AICompletion;
import jpaoletti.jpm2.core.ai.AIConnectorConfig;
import jpaoletti.jpm2.core.ai.AIException;
import jpaoletti.jpm2.core.ai.AIProviderImplementation;
import jpaoletti.jpm2.core.ai.AIRequest;
import jpaoletti.jpm2.core.crypto.SysparamCipher;
import jpaoletti.jpm2.core.dao.DefaultJPADAO;
import jpaoletti.jpm2.core.log.DebugLog;
import jpaoletti.jpm2.core.model.persistent.AICallLog;
import jpaoletti.jpm2.core.model.persistent.AIConnector;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Orchestrates AI completions over administrable {@link AIConnector}s: resolves the connector (by code or
 * logical purpose), decrypts its credentials into an {@link AIConnectorConfig}, dispatches to the matching
 * {@link AIProviderImplementation} (resolved by provider code), applies the model fallback chain, and records
 * every attempt in {@link AICallLog}.
 *
 * <p>Wired as an XML bean (like {@code MailSenderService}); the provider implementations are injected as a
 * list via {@link #setProviders(List)}. The {@code SysparamCipher} is optional — where absent, encrypted
 * parameter values are treated as plaintext (the cipher's own contract is a plaintext passthrough anyway).
 *
 * @author jpaoletti
 */
public class AIService {

    /** Debug channel for the whole AI module (turn on with debug level for channel {@code ai-connector}). */
    public static final String DEBUG_CHANNEL = "ai-connector";

    @Autowired
    @Qualifier(value = "dao-aiConnector")
    private DefaultJPADAO connectorDAO;

    @Autowired
    @Qualifier(value = "dao-aiCallLog")
    private DefaultJPADAO callLogDAO;

    @Autowired(required = false)
    private SysparamCipher cipher;

    private final Map<String, AIProviderImplementation> providers = new LinkedHashMap<>();

    /** Registers the available provider implementations, keyed by {@link AIProviderImplementation#code()}. */
    public void setProviders(List<AIProviderImplementation> list) {
        providers.clear();
        if (list != null) {
            for (AIProviderImplementation provider : list) {
                providers.put(provider.code(), provider);
            }
        }
    }

    /** Runs a completion through the connector identified by {@code connectorCode}. */
    public AICompletion complete(String connectorCode, AIRequest request) throws AIException {
        final AIConnector connector = findByCode(connectorCode);
        if (connector == null) {
            throw new AIException("Unknown AI connector: " + connectorCode);
        }
        return complete(connector, request);
    }

    /** Runs a completion through the first active connector serving {@code purpose}. */
    public AICompletion completeForPurpose(String purpose, AIRequest request) throws AIException {
        DebugLog.debug(DEBUG_CHANNEL, 1, () -> "completeForPurpose('" + purpose + "')");
        final AIConnector connector = findByPurpose(purpose);
        if (connector == null) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "no active connector for purpose '" + purpose + "'");
            throw new AIException("No active AI connector for purpose: " + purpose);
        }
        return complete(connector, request);
    }

    /** Runs a completion through a resolved connector. Tries the model fallback chain in order. */
    public AICompletion complete(AIConnector connector, AIRequest request) throws AIException {
        if (connector == null || !connector.isActive()) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "connector is null or inactive: " + (connector == null ? "null" : connector.getCode()));
            throw new AIException("AI connector is not active");
        }
        DebugLog.debug(DEBUG_CHANNEL, 1, () -> "connector '" + connector.getCode() + "' type=" + connector.getType()
                + " purpose=" + connector.getPurpose());
        final AIProviderImplementation provider = providers.get(connector.getType().code());
        if (provider == null) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "no provider impl for type " + connector.getType()
                    + " (registered: " + providers.keySet() + ")");
            throw new AIException("No provider implementation registered for type " + connector.getType());
        }
        final AIConnectorConfig config;
        try {
            config = connector.toConfig(cipher);
        } catch (PMException e) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "toConfig failed: " + e.getMessage());
            throw new AIException("Could not resolve connector configuration: " + e.getMessage(), e);
        }
        DebugLog.debug(DEBUG_CHANNEL, 2, () -> "config baseUrl=" + config.getBaseUrl() + " defaultModel=" + config.getDefaultModel()
                + " paramKeys=" + config.getParameters().keySet() + " apiKeySet="
                + (config.getApiKey() != null && !config.getApiKey().isBlank()) + " cipher="
                + (cipher != null ? cipher.getClass().getSimpleName() : "none"));
        DebugLog.debug(DEBUG_CHANNEL, 2, () -> "request messages=" + request.getMessages().size()
                + " systemLen=" + (request.getSystem() == null ? 0 : request.getSystem().length())
                + " maxTokens=" + request.getMaxTokens() + " temperature=" + request.getTemperature()
                + " hasSchema=" + (request.getJsonSchema() != null && !request.getJsonSchema().isBlank()));
        final List<String> models = resolveModels(connector, request);
        DebugLog.debug(DEBUG_CHANNEL, 1, () -> "models to try: " + models);
        AIException last = null;
        for (String model : models) {
            DebugLog.debug(DEBUG_CHANNEL, 1, () -> "attempt model=" + model);
            final AIRequest attempt = withModel(request, model);
            final long start = System.currentTimeMillis();
            try {
                final AICompletion completion = provider.complete(config, attempt);
                final long latency = System.currentTimeMillis() - start;
                DebugLog.debug(DEBUG_CHANNEL, 1, () -> "OK model=" + model + " refusal=" + completion.isRefusal()
                        + " finish=" + completion.getFinishReason()
                        + " in=" + (completion.getUsage() != null ? completion.getUsage().getPromptTokens() : 0)
                        + " out=" + (completion.getUsage() != null ? completion.getUsage().getCompletionTokens() : 0)
                        + " latencyMs=" + latency);
                DebugLog.debug(DEBUG_CHANNEL, 3, () -> "response text: " + completion.getText());
                log(connector, model, completion, null, latency);
                return completion;
            } catch (AIException e) {
                final long latency = System.currentTimeMillis() - start;
                last = e;
                DebugLog.debug(DEBUG_CHANNEL, 1, () -> "ERROR model=" + model + " latencyMs=" + latency + ": " + e.getMessage());
                log(connector, model, null, e, latency);
            }
        }
        DebugLog.debug(DEBUG_CHANNEL, 1, () -> "all models exhausted; no completion");
        throw (last != null) ? last : new AIException("No model produced a completion");
    }

    private List<String> resolveModels(AIConnector connector, AIRequest request) {
        final List<String> models = new ArrayList<>();
        if (request.getModel() != null && !request.getModel().isBlank()) {
            models.add(request.getModel());
        } else if (connector.getDefaultModel() != null && !connector.getDefaultModel().isBlank()) {
            models.add(connector.getDefaultModel());
        }
        if (connector.getFallbackModels() != null) {
            for (String candidate : connector.getFallbackModels().split(",")) {
                final String trimmed = candidate.trim();
                if (!trimmed.isEmpty() && !models.contains(trimmed)) {
                    models.add(trimmed);
                }
            }
        }
        if (models.isEmpty()) {
            models.add(null); // let the provider apply its own default (e.g. Claude -> claude-opus-5)
        }
        return models;
    }

    private AIRequest withModel(AIRequest source, String model) {
        final AIRequest.Builder builder = AIRequest.builder()
                .messages(source.getMessages())
                .model(model)
                .maxTokens(source.getMaxTokens())
                .temperature(source.getTemperature())
                .system(source.getSystem())
                .jsonSchema(source.getJsonSchema());
        for (Map.Entry<String, Object> entry : source.getExtras().entrySet()) {
            builder.extra(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private AIConnector findByCode(String code) {
        if (code == null) {
            return null;
        }
        for (Object candidate : connectorDAO.list(null)) {
            final AIConnector connector = (AIConnector) candidate;
            if (code.equals(connector.getCode())) {
                return connector;
            }
        }
        return null;
    }

    private AIConnector findByPurpose(String purpose) {
        if (purpose == null) {
            return null;
        }
        for (Object candidate : connectorDAO.list(null)) {
            final AIConnector connector = (AIConnector) candidate;
            if (connector.isActive() && purpose.equals(connector.getPurpose())) {
                return connector;
            }
        }
        return null;
    }

    private void log(AIConnector connector, String model, AICompletion completion, AIException error, long latencyMs) {
        try {
            final AICallLog entry = new AICallLog();
            entry.setConnectorCode(connector.getCode());
            entry.setPurpose(connector.getPurpose());
            entry.setModel(model);
            entry.setLatencyMs(latencyMs);
            if (error != null) {
                entry.setStatus("ERROR");
                entry.setErrorMessage(truncate(error.getMessage(), 1000));
            } else if (completion != null) {
                entry.setStatus(completion.isRefusal() ? "REFUSAL" : "OK");
                if (completion.getUsage() != null) {
                    entry.setPromptTokens(completion.getUsage().getPromptTokens());
                    entry.setCompletionTokens(completion.getUsage().getCompletionTokens());
                }
                entry.setResponseSummary(truncate(completion.getText(), 4000));
            }
            callLogDAO.save(entry);
        } catch (Exception e) {
            JPMUtils.getLogger().warn("Could not persist AI call log", e);
        }
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() > max ? value.substring(0, max) : value;
    }
}
