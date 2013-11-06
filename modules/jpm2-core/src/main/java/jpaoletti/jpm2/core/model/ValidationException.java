package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;

/**
 *
 * @author jpaoletti
 */
public class ValidationException extends PMException {

    private Object validatedObject;

    public ValidationException(Object validatedObject) {
        this((Message) null);
        this.validatedObject = validatedObject;
    }

    public ValidationException(Message msg) {
        super(msg);
    }

    public Object getValidatedObject() {
        return validatedObject;
    }

    public void setValidatedObject(Object validatedObject) {
        this.validatedObject = validatedObject;
    }

}
