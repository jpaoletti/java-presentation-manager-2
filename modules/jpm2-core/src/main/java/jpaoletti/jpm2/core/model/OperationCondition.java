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
     */
    public boolean check(final EntityInstance instance, final Operation operation, final String displayAt) throws PMException;
}
