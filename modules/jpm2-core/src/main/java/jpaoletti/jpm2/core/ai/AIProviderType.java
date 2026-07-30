package jpaoletti.jpm2.core.ai;

/**
 * Catalog of supported AI providers. Each value carries the stable {@code code} that matches the
 * {@link AIProviderImplementation#code()} of its implementation bean; {@code AIService} resolves the
 * implementation by that code (same enum-to-bean idea as the currency-converter catalog).
 */
public enum AIProviderType {

    CLAUDE("claude"),
    OPENAI("openai"),
    GEMINI("gemini");

    private final String code;

    AIProviderType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
