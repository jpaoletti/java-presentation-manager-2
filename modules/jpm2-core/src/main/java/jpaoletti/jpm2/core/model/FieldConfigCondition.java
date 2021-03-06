package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMException;

/**
 * Condition for matching of field config.
 *
 * @author jpaoletti
 */
public interface FieldConfigCondition {

    /**
     * Instance is posible to be null in general operations
     *
     * @param instance
     * @param operation
     * @return
     * @throws jpaoletti.jpm2.core.PMException
     */
    public boolean check(final EntityInstance instance, final Operation operation) throws PMException;
}
