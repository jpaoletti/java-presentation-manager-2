package jpaoletti.jpm2.core.exception;

/**
 * Engine exception that indicates a missconfiguration of jPM.
 *
 * @author jpaoletti
 */
public class EntityNotFoundException extends ConfigurationException {

    public EntityNotFoundException(String key) {
        super("Entity not found: " + key);
    }
}
