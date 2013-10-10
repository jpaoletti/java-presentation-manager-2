package jpaoletti.jpm2.core.exception;

import jpaoletti.jpm2.core.PMException;

/**
 * Engine exception that indicates a missconfiguration of jPM.
 *
 * @author jpaoletti
 */
public class EntityNotFoundException extends PMException {

    public EntityNotFoundException(String key) {
        super("Entity not found: " + key);
    }
}
