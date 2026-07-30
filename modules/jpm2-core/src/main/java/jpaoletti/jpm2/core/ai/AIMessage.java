package jpaoletti.jpm2.core.ai;

/**
 * A single conversation message, provider-neutral.
 */
public class AIMessage {

    private final AIRole role;
    private final String content;

    public AIMessage(AIRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public static AIMessage system(String content) {
        return new AIMessage(AIRole.SYSTEM, content);
    }

    public static AIMessage user(String content) {
        return new AIMessage(AIRole.USER, content);
    }

    public static AIMessage assistant(String content) {
        return new AIMessage(AIRole.ASSISTANT, content);
    }

    public AIRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
