package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import static jpaoletti.jpm2.controller.OperationAction.COMMIT_ERROR;
import jpaoletti.jpm2.core.PMException;

/**
 *
 * @author jpaoletti
 */
public class EditAction extends OperationAction {

    public EditAction() {
        this.requireObject = true;
    }

    public String commit() throws PMException {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            processFields();
            if (!getFieldMessages().isEmpty()) {
                return COMMIT_ERROR;
            }
            preExecute();
            getEntity().getDao().update(getObject());
            postExecute();
            return FINISH;
        } else {
            return prepare;
        }
    }
}
