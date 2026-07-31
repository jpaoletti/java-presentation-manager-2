package jpaoletti.jpm2.core.ai;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Input handed to an {@link AIContextProvider} when {@code AIService} gathers context for a completion.
 * Carries the logical {@code purpose} of the call, the resolved user {@code input} text, and an
 * {@code attributes} bag the caller populated on the {@link AIRequest} (e.g. the domain object or ids the
 * provider needs to build its context). The core stays domain-agnostic: only the concrete provider knows
 * what to read from {@code attributes}.
 */
public class AIContextRequest {

    private final String purpose;
    private final String input;
    private final Map<String, Object> attributes;

    public AIContextRequest(String purpose, String input, Map<String, Object> attributes) {
        this.purpose = purpose;
        this.input = input;
        this.attributes = (attributes != null)
                ? Collections.unmodifiableMap(new LinkedHashMap<>(attributes))
                : Collections.emptyMap();
    }

    public String getPurpose() {
        return purpose;
    }

    /** The concatenated user-message text of the request (never null; may be empty). */
    public String getInput() {
        return input;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
