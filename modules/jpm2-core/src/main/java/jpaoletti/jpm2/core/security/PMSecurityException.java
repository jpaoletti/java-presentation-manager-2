package jpaoletti.jpm2.core.security;

import jpaoletti.jpm2.core.PMException;

public class PMSecurityException extends PMException {
    private static final long serialVersionUID = 436149144383856197L;

    public PMSecurityException(String s, Throwable nested) {
        super(s, nested);
    }

    public PMSecurityException(Throwable nested) {
        super(nested);
    }

    public PMSecurityException() {
    }

    public PMSecurityException(String key) {
        super(key);
    }
}
