package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;

/**
 *
 * @author jpaoletti
 */
public class DefaultCrudAction extends OperationAction {

    //Parameters
    private String instanceId;
    //Result
    private EntityInstance instance;
    private List<Operation> itemOperations;
    //Internal
    private Object object = null;

    @Override
    protected String prepare() throws PMException {
        final String prepare = super.prepare();
        if (SUCCESS.equals(prepare)) {
            if (getInstanceId() != null) {
                object = getEntity().getDao().get(getInstanceId());
                if (object != null) {
                    setInstance(new EntityInstance(getInstanceId(), getEntity(), getOperation(), object));
                }
            }
            this.itemOperations = getEntity().getOperationsFor(object, getOperation(), OperationScope.ITEM);
        }
        return prepare;
    }

    @Override
    public String execute() throws Exception {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            return SUCCESS;
        } else {
            return prepare;
        }
    }

    public EntityInstance getInstance() {
        return instance;
    }

    public void setInstance(EntityInstance instance) {
        this.instance = instance;
    }

    public List<Operation> getItemOperations() {
        return itemOperations;
    }

    public void setItemOperations(List<Operation> itemOperations) {
        this.itemOperations = itemOperations;
    }

    protected Object getObject() {
        return object;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
