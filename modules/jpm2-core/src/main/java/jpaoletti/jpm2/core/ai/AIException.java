package jpaoletti.jpm2.core.ai;

/**
 * Raised when an {@link AIProviderImplementation} cannot fulfill a request (transport failure, non-2xx
 * response, malformed configuration, unsupported capability). A safety-policy decline is NOT an exception:
 * it is returned as an {@link AICompletion} with {@code refusal == true}.
 */
public class AIException extends Exception {

    public AIException(String message) {
        super(message);
    }

    public AIException(String message, Throwable cause) {
        super(message, cause);
    }
}
