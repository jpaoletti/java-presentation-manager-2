package jpaoletti.jpm2.core.ai;

/**
 * Role of a message in a conversation, provider-neutral. Each {@link AIProviderImplementation} maps these
 * to its own wire vocabulary (e.g. Anthropic folds {@code SYSTEM} into a top-level {@code system} field).
 */
public enum AIRole {
    SYSTEM,
    USER,
    ASSISTANT
}
