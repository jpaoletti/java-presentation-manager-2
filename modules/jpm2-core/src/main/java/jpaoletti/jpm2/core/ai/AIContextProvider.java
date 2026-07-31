package jpaoletti.jpm2.core.ai;

import java.util.List;

/**
 * SPI for contributing context to an AI completion. Modules register {@code AIContextProvider} beans; before
 * dispatching a completion, {@code AIService} asks every provider that {@link #supports(String) supports} the
 * call's purpose to {@link #contribute(AIContextRequest) contribute} snippets, which are then injected into
 * the prompt. The core ships no provider — each module implements its own (keyword/SQL retrieval, tenant
 * info, catalog context, ...) and stays the only place that knows the domain.
 */
public interface AIContextProvider {

    /** Whether this provider contributes to the given logical purpose. */
    boolean supports(String purpose);

    /**
     * Builds context snippets for the request. Return an empty list (or {@code null}) to contribute nothing.
     * Implementations must not throw for a missing/partial context — return what they have; a thrown
     * exception is logged and skipped, it does not fail the completion.
     */
    List<String> contribute(AIContextRequest request);
}
