package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;
import static jpaoletti.jpm2.controller.BaseAction.getBean;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;

/**
 *
 * @author jpaoletti
 */
public class OperationAction extends BaseAction {

    //Parameters
    private String entityId;
    //Results
    private Entity entity;
    private Operation operation;
    private List<Operation> generalOperations;
    private List<Operation> selectedOperations;

    /**
     * Loads entity and operation.
     */
    protected String prepare() throws PMException {
        if (getEntityId() == null || getEntityId().trim().equals("")) {
            getActionErrors().add(UNDEFINED_ENTITY_PARAMETER);
            return ERROR;
        }
        entity = (Entity) getBean(getEntityId());
        if (entity == null) {
            getActionErrors().add(UNDEFINED_ENTITY);
            return ERROR;
        }
        operation = getEntity().getOperation(getActionName());
        if (operation == null) {
            getActionErrors().add(UNDEFINED_OPERATION);
            return ERROR;
        }
        this.generalOperations = entity.getOperationsFor(null, getOperation(), OperationScope.GENERAL);
        this.selectedOperations = entity.getOperationsFor(null, getOperation(), OperationScope.SELECTED);
        return SUCCESS;
    }

    public List<Operation> getGeneralOperations() {
        return generalOperations;
    }

    public List<Operation> getSelectedOperations() {
        return selectedOperations;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Operation getOperation() {
        return operation;
    }
}
