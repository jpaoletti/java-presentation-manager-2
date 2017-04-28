package jpaoletti.jpm2.core.exception;

import jpaoletti.jpm2.core.PMException;

/**
 *
 * @author jpaoletti
 */
public class NotAuthorizedException extends PMException {

    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String key) {
        super(key);
    }

    @Override
    public String toString() {
        return "NotAuthorizedException{" + getMsg().getText() + '}';
    }

}
