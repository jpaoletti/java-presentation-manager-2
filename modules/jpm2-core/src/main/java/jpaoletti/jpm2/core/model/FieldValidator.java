package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.message.Message;

/**
 *
 * @author jpaoletti
 */
public interface FieldValidator {

    public Message validate(Object object, Object convertedValue);
}
