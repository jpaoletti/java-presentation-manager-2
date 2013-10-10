package jpaoletti.jpm2.core.exception;

/**
 *
 * @author jpaoletti
 */
public class FieldNotFoundException extends ConfigurationException {

    public FieldNotFoundException(String entityid, String key) {
        super("Field not found: " + entityid + "." + key);
    }
}
