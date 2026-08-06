package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMException;

/**
 * This interface is intended to determine if an operation should be displayed
 * on other or not depending on this conditional
 *
 * @author jpaoletti
 */
public interface OperationCondition {

    /**
     * object is posible to be null.
     *
     * <p>
     * Instead of just returning <code>false</code>, an implementation may throw
     * a {@link jpaoletti.jpm2.core.exception.ConditionNotMetException} with its
     * own i18n key to explain which of its checks failed. While rendering a
     * list that is equivalent to returning <code>false</code> (the operation is
     * not displayed); when the operation is accessed directly, the given
     * message is shown instead of the generic access denied one, and it takes
     * precedence over the <code>deniedMessageKey</code> of the operation. The
     * same goes for <code>goTo(operationId)</code>, which offers a link out of
     * the denial page and overrides <code>deniedGoToOperation</code>.
     *
     * @param instance
     * @param operation
     * @param displayAt
     * @return
     * @throws jpaoletti.jpm2.core.PMException
     */
    public boolean check(final EntityInstance instance, final Operation operation, final String displayAt) throws PMException;
}
