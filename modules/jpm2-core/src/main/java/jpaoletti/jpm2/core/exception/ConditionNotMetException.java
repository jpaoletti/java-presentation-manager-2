package jpaoletti.jpm2.core.exception;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;

/**
 * Thrown when the condition of an operation is not met. It carries an optional
 * user facing message explaining the reason of the denial, so the application
 * can show something more useful than the generic "access denied" page.
 *
 * <p>
 * The message can come from two places:
 * <ul>
 * <li>the <code>deniedMessageKey</code> property of the
 * {@link jpaoletti.jpm2.core.model.Operation}, used when the condition simply
 * returns <code>false</code>;</li>
 * <li>the condition itself, throwing this exception with its own key and
 * arguments to tell apart which of its checks failed.</li>
 * </ul>
 *
 * <p>
 * As it extends {@link NotAuthorizedException}, throwing it while rendering a
 * list is equivalent to returning <code>false</code>: the operation is just not
 * displayed.
 *
 * @author jpaoletti
 */
public class ConditionNotMetException extends NotAuthorizedException {

    /**
     * Id of an operation of the same entity offered as a link on the denial
     * page, so the user has somewhere to go back to.
     */
    private String goToOperation;

    public ConditionNotMetException() {
    }

    /**
     * @param key i18n key of the message to show, may be null
     * @param args message arguments
     */
    public ConditionNotMetException(String key, String... args) {
        if (key != null) {
            setMsg(MessageFactory.error(key, args));
        }
    }

    public ConditionNotMetException(Message msg) {
        setMsg(msg);
    }

    /**
     * Offers a link to another operation of the same entity on the denial page.
     * Meant to be chained: <code>throw new
     * ConditionNotMetException("my.key").goTo("show");</code>
     *
     * @param operationId id of the operation to link to
     * @return this
     */
    public ConditionNotMetException goTo(String operationId) {
        this.goToOperation = operationId;
        return this;
    }

    public String getGoToOperation() {
        return goToOperation;
    }

    public void setGoToOperation(String goToOperation) {
        this.goToOperation = goToOperation;
    }

    /**
     * Conditions are evaluated once per row while rendering a list, so this
     * exception is stackless to keep it cheap as a control flow signal.
     *
     * @return this
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String toString() {
        if (getMsg() != null) {
            return "ConditionNotMetException{" + getMsg().getKey() + '}';
        } else {
            return "ConditionNotMetException";
        }
    }
}
