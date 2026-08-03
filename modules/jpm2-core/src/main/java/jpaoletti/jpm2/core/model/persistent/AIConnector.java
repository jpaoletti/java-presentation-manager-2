package jpaoletti.jpm2.core.model.persistent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.ai.AIConnectorConfig;
import jpaoletti.jpm2.core.ai.AIProviderType;
import jpaoletti.jpm2.core.crypto.SysparamCipher;
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

/**
 * Administrable AI provider connection. One row = one configured connection to a provider (Claude, OpenAI,
 * Gemini, ...). Credentials live in {@link AIConnectorParameter} children (the {@code api-key} parameter is
 * stored encrypted); {@link #toConfig(SysparamCipher)} resolves them into the provider-neutral
 * {@link AIConnectorConfig} that implementations consume.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "ai_connectors")
public class AIConnector extends JPMPersistentObject implements ParameterizedEntity<AIConnectorParameter> {

    /** Name of the parameter that holds the (encrypted) API key. */
    public static final String API_KEY_PARAM = "api-key";

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;
    private String description;

    /** Logical purpose this connector serves (e.g. {@code sales-intake}); lets modules avoid hardcoding a provider. */
    private String purpose;

    @Enumerated
    @Column(name = "connector_type")
    private AIProviderType type = AIProviderType.CLAUDE;

    @Column(name = "default_model")
    private String defaultModel;

    /** CSV of fallback model ids tried in order when a call fails. */
    @Column(name = "fallback_models")
    private String fallbackModels;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "timeout_ms")
    private Integer timeoutMs;

    @Type(type = "yes_no")
    private boolean active = true;

    @OneToMany(mappedBy = "connector", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<AIConnectorParameter> parameters = new ArrayList<>();

    /**
     * Resolves this connector into a provider-neutral runtime configuration, decrypting secret parameters.
     *
     * @param cipher cipher used to decrypt encrypted parameter values; may be {@code null} (values are then
     *               treated as plaintext). {@link SysparamCipher#decrypt(String)} is a passthrough for
     *               non-encrypted values, so plaintext api-keys work with or without a cipher.
     */
    public AIConnectorConfig toConfig(SysparamCipher cipher) throws PMException {
        final Map<String, String> resolved = new LinkedHashMap<>();
        String apiKey = null;
        for (AIConnectorParameter parameter : parameters) {
            final String name = parameter.getName();
            if (name == null) {
                continue;
            }
            String value = parameter.getValue();
            if (value != null && cipher != null) {
                value = cipher.decrypt(value);
            }
            if (API_KEY_PARAM.equals(name)) {
                apiKey = value;
            } else {
                resolved.put(name, value);
            }
        }
        if (timeoutMs != null && !resolved.containsKey("timeout-ms")) {
            resolved.put("timeout-ms", String.valueOf(timeoutMs));
        }
        return new AIConnectorConfig(apiKey, baseUrl, defaultModel, resolved);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public AIProviderType getType() {
        return type;
    }

    public void setType(AIProviderType type) {
        this.type = type;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public String getFallbackModels() {
        return fallbackModels;
    }

    public void setFallbackModels(String fallbackModels) {
        this.fallbackModels = fallbackModels;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<AIConnectorParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<AIConnectorParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getParameterKind() {
        return AIProviderType.KIND;
    }

    /** Parameters of the selected provider ({@link AIProviderType}); {@code api-key} is secret. */
    @Override
    public List<EntityParameterDef<?>> parameterCatalog() {
        return type != null ? type.parameterDefs() : Collections.emptyList();
    }

    @Override
    public AIConnectorParameter newParameter(String name, String value) {
        final AIConnectorParameter parameter = new AIConnectorParameter();
        parameter.setName(name);
        parameter.setValue(value);
        parameter.setConnector(this);
        return parameter;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof AIConnector;
    }
}
