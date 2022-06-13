package jpaoletti.jpm2.core.exception;

/**
 * Engine exception that indicates a missconfiguration of jPM.
 *
 * @author jpaoletti
 */
public class DBConstraintException extends ConfigurationException {

    public DBConstraintException(String key) {
        super(key);
    }
}
