package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import static jpaoletti.jpm2.controller.OperationAction.FINISH;
import jpaoletti.jpm2.core.PMException;

/**
 *
 * @author jpaoletti
 */
public class DeleteAction extends OperationAction {

    public DeleteAction() {
        this.requireObject = true;
    }

    @Override
    public String execute() throws Exception {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            preExecute();
            getEntity().getDao().delete(getObject());
            postExecute();
            return FINISH;
        } else {
            return prepare;
        }
    }
}
