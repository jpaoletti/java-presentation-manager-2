package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import static jpaoletti.jpm2.controller.OperationAction.FINISH;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class AddAction extends OperationAction {

    public AddAction() {
        this.requireObject = false;
    }

    @Override
    protected String prepare() throws PMException {
        final String prepare = super.prepare();
        if (SUCCESS.equals(prepare)) {
            setObject(JPMUtils.newInstance(getEntity().getClazz()));
            setInstance(new EntityInstance(getInstanceId(), getEntity(), getOperation(), getObject()));
            setItemOperations(getEntity().getOperationsFor(getObject(), getOperation(), OperationScope.ITEM));
        }
        return prepare;
    }

    public String commit() throws PMException {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            try {
                processFields();
                preExecute();
            } catch (ValidationException e) {
                if (e.getMsg() != null) {
                    getEntityMessages().add(e.getMsg());
                }
                return COMMIT_ERROR;
            }
            getEntity().getDao().save(getObject());
            setInstanceId(getEntity().getDao().getId(getObject()).toString());
            postExecute();
            return FINISH;
        } else {
            return prepare;
        }
    }
}
