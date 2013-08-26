package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;

/**
 *
 * @author jpaoletti
 */
public class ValidationException extends PMException {

    public ValidationException(Message msg) {
        super(msg);
    }
}
