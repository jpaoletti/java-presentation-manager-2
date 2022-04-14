package jpaoletti.jpm2.web;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;

/**
 *
 * @author jpaoletti
 */
public class JPMAskConfirmationException extends PMException {

    public JPMAskConfirmationException(Message msg) {
        super(msg);
    }

    public JPMAskConfirmationException(String key) {
        super(key);
    }

}
