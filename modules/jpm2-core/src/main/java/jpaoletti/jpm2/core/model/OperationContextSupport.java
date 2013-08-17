package jpaoletti.jpm2.core.model;

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
    public void preConversion() throws PMException {
    }

    @Override
    public void preExecute() throws PMException {
    }

    @Override
    public void postExecute() throws PMException {
    }
}
