package jpaoletti.jpm2.core;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;

/**
 * A generic expection for Presentation Manager engine.
 *
 * @author jpaoletti
 */
public class PMException extends Exception {

    private Message msg;

    public PMException(Message msg) {
        this.msg = msg;
    }

    public PMException(Message msg, Throwable nested) {
        super(nested);
        this.msg = msg;
    }

    /**
     *
     * @param key
     */
    public PMException(String key) {
        this.msg = MessageFactory.error(key);
    }

    /**
     *
     */
    public PMException() {
        super();
    }

    /**
     *
     * @param nested
     */
    public PMException(Throwable nested) {
        super(nested);
    }

    /**
     *
     * @param s
     * @param nested
     */
    public PMException(String s, Throwable nested) {
        this(nested);
        this.msg = MessageFactory.error(s);
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return (getMsg() != null) ? getMsg().getKey() : "";
    }
}
