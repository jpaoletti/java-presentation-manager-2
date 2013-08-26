package jpaoletti.jpm2.core.model;

/**
 *
 * @author jpaoletti
 */
public interface OperationValidator {

    public void validate(Object object) throws ValidationException;
}
