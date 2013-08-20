package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.OperationController;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.PMException;

/**
 * Support class for OperationContext.
 *
 * @see OperationContext
 *
 * @author jpaoletti
 */
public class OperationContextSupport extends PMCoreObject implements OperationContext {

    @Override
    public void preConversion(OperationController operationController) throws PMException {
    }

    @Override
    public void preExecute(OperationController operationController) throws PMException {
    }

    @Override
    public void postExecute(OperationController operationController) throws PMException {
    }
}
