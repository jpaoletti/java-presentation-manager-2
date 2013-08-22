package jpaoletti.jpm2.core.converter;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;

/**
 * An exception thrown if a conversion fail
 *
 * @author jpaoletti
 */
public class ConverterException extends PMException {

    public ConverterException(Message msg) {
        super(msg);
    }

    public ConverterException(String key) {
        super(key);
    }
}
