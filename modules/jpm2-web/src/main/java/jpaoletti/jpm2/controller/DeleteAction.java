package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import static jpaoletti.jpm2.controller.OperationAction.COMMIT_ERROR;
import static jpaoletti.jpm2.controller.OperationAction.FINISH;
import jpaoletti.jpm2.core.model.ValidationException;

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
            try {
                preExecute();
            } catch (ValidationException e) {
                getEntityMessages().add(e.getMsg());
                return COMMIT_ERROR;
            }
            getEntity().getDao().delete(getObject());
            postExecute();
            return FINISH;
        } else {
            return prepare;
        }
    }
}
