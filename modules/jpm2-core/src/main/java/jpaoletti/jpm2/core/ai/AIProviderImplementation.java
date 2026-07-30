package jpaoletti.jpm2.core.ai;

/**
 * Pluggable contract for a concrete AI provider (Claude, OpenAI, Gemini, ...). This is the AI-module analog
 * of {@code GatewayProtocolImplementation} / {@code CurrencyConverter}: implementations are Spring beans
 * resolved by an {@code AIProviderType} enum, and every one maps the provider-neutral {@link AIRequest} to
 * its own wire format and the wire response back to a neutral {@link AICompletion}.
 *
 * <p>Keep implementations thin: model CHAT and STRUCTURED_OUTPUT, and let uncommon provider-specific knobs
 * travel through {@link AIRequest#getExtras()} rather than growing the neutral DTO for every feature.
 */
public interface AIProviderImplementation {

    /** Stable provider code, e.g. {@code "claude"}. Used to resolve/select the implementation. */
    String code();

    /**
     * Runs a synchronous completion.
     *
     * @param config the resolved connector configuration (decrypted credentials + settings)
     * @param request the provider-neutral request
     * @return the provider-neutral completion (a safety-policy decline arrives here with
     *         {@link AICompletion#isRefusal()} true, not as an exception)
     * @throws AIException on transport failure, non-2xx response, or invalid configuration
     */
    AICompletion complete(AIConnectorConfig config, AIRequest request) throws AIException;

    /** Whether this provider supports a given capability. Defaults to CHAT + STRUCTURED_OUTPUT. */
    default boolean supports(AICapability capability) {
        return capability == AICapability.CHAT || capability == AICapability.STRUCTURED_OUTPUT;
    }
}
