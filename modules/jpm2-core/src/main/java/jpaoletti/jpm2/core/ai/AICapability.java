package jpaoletti.jpm2.core.ai;

/**
 * Provider-neutral capability flags. A concrete {@link AIProviderImplementation} declares which of these
 * it supports so callers can degrade gracefully instead of failing at request time.
 */
public enum AICapability {
    CHAT,
    STRUCTURED_OUTPUT,
    TOOLS,
    EMBEDDINGS,
    VISION,
    STREAMING
}
