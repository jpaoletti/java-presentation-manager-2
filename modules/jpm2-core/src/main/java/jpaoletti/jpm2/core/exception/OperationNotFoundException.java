package jpaoletti.jpm2.core.exception;

/**
 *
 * @author jpaoletti
 */
public class OperationNotFoundException extends ConfigurationException {

    public OperationNotFoundException(String entityid, String key) {
        super("Operation not found: " + entityid + "." + key);
    }
}
