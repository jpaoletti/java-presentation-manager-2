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
            if (getInstanceId() != null && !getInstanceId().trim().equalsIgnoreCase("")) {
                setObject(getEntity().getDao().get(getInstanceId()));
                if (getObject() != null) {
                    setInstance(new EntityInstance(getInstanceId(), getEntity(), getOperation(), getObject()));
                }
            }
            setItemOperations(getEntity().getOperationsFor(object, getOperation(), OperationScope.ITEM));
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

    protected void setObject(Object object) {
        this.object = object;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
