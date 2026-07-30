package jpaoletti.jpm2.core.ai;

/**
 * Provider-neutral completion result.
 *
 * <p>{@code text} is the assistant's text output (empty on a pre-output refusal). {@code structuredJson} is
 * populated only when the request asked for structured output and the call was not refused. {@code refusal}
 * flags a safety-policy decline (a successful HTTP call whose {@code finishReason} is {@code "refusal"}).
 * {@code raw} keeps the untouched provider payload for logging/debugging.
 */
public class AICompletion {

    private final String text;
    private final String structuredJson;
    private final String model;
    private final AIUsage usage;
    private final String finishReason;
    private final boolean refusal;
    private final String raw;

    public AICompletion(String text, String structuredJson, String model, AIUsage usage,
                        String finishReason, boolean refusal, String raw) {
        this.text = text;
        this.structuredJson = structuredJson;
        this.model = model;
        this.usage = usage;
        this.finishReason = finishReason;
        this.refusal = refusal;
        this.raw = raw;
    }

    public String getText() {
        return text;
    }

    public String getStructuredJson() {
        return structuredJson;
    }

    public String getModel() {
        return model;
    }

    public AIUsage getUsage() {
        return usage;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public boolean isRefusal() {
        return refusal;
    }

    public String getRaw() {
        return raw;
    }
}
