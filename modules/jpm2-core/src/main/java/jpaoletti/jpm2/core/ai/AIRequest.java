package jpaoletti.jpm2.core.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Provider-neutral completion request. Build it with {@link #builder()}.
 *
 * <p>{@code system} is the system prompt (providers place it wherever their API expects). {@code jsonSchema}
 * requests structured output constrained to that JSON Schema. {@code extras} is a provider-specific
 * passthrough: keys placed here are merged into the outbound request body verbatim, so callers can reach
 * knobs the neutral DTO does not model (at their own risk regarding provider validation).
 */
public class AIRequest {

    private final List<AIMessage> messages;
    private final String model;
    private final Integer maxTokens;
    private final Double temperature;
    private final String system;
    private final String jsonSchema;
    private final Map<String, Object> extras;
    private final Map<String, Object> attributes;

    private AIRequest(Builder builder) {
        this.messages = Collections.unmodifiableList(new ArrayList<>(builder.messages));
        this.model = builder.model;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
        this.system = builder.system;
        this.jsonSchema = builder.jsonSchema;
        this.extras = Collections.unmodifiableMap(new LinkedHashMap<>(builder.extras));
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(builder.attributes));
    }

    public List<AIMessage> getMessages() {
        return messages;
    }

    public String getModel() {
        return model;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getSystem() {
        return system;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    /** Caller-supplied payload for {@link AIContextProvider}s (domain object, ids, ...). Not sent to the provider. */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<AIMessage> messages = new ArrayList<>();
        private String model;
        private Integer maxTokens;
        private Double temperature;
        private String system;
        private String jsonSchema;
        private final Map<String, Object> extras = new LinkedHashMap<>();
        private final Map<String, Object> attributes = new LinkedHashMap<>();

        public Builder message(AIMessage message) {
            this.messages.add(message);
            return this;
        }

        public Builder messages(List<AIMessage> messages) {
            this.messages.addAll(messages);
            return this;
        }

        public Builder user(String content) {
            return message(AIMessage.user(content));
        }

        public Builder assistant(String content) {
            return message(AIMessage.assistant(content));
        }

        public Builder systemMessage(String content) {
            return message(AIMessage.system(content));
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder system(String system) {
            this.system = system;
            return this;
        }

        public Builder jsonSchema(String jsonSchema) {
            this.jsonSchema = jsonSchema;
            return this;
        }

        public Builder extra(String key, Object value) {
            this.extras.put(key, value);
            return this;
        }

        /** Attaches a payload for {@link AIContextProvider}s (not sent to the provider). */
        public Builder attribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }

        public AIRequest build() {
            return new AIRequest(this);
        }
    }
}
