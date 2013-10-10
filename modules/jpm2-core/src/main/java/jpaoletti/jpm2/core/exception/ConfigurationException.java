package jpaoletti.jpm2.core.exception;

import jpaoletti.jpm2.core.PMException;

/**
 * Engine exception that indicates a missconfiguration of jPM.
 *
 * @author jpaoletti
 */
public class ConfigurationException extends PMException {

    public ConfigurationException(String key) {
        super(key);
    }
}
