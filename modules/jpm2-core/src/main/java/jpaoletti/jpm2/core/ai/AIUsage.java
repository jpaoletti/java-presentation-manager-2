package jpaoletti.jpm2.core.ai;

/**
 * Token accounting for a single completion. The basis for cost tracking (see the planned {@code AICallLog}).
 */
public class AIUsage {

    private final int promptTokens;
    private final int completionTokens;

    public AIUsage(int promptTokens, int completionTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public int getTotalTokens() {
        return promptTokens + completionTokens;
    }
}
