package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;
import java.util.Map;
import static jpaoletti.jpm2.controller.BaseAction.UNDEFINED_OBJECT;
import static jpaoletti.jpm2.controller.BaseAction.UNDEFINED_OPERATION;
import jpaoletti.jpm2.core.OperationController;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.dao.GenericDAO;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.FieldValidator;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class OperationAction extends EntityAction implements OperationController {

    public static final String COMMIT_ERROR = "commit_error";
    public static final String FINISH = "finish";
    //Parameters
    private String instanceId;
    //Results
    private Operation operation;
    private EntityInstance instance;
    private List<Operation> itemOperations;
    private List<Operation> generalOperations;
    private List<Operation> selectedOperations;
    //Internal
    private Object object = null;
    protected boolean requireObject = false;

    /**
     * Loads entity and operation.
     */
    protected String prepare() throws PMException {
        final String loadEntity = loadEntity();
        if (!loadEntity.equals(SUCCESS)) {
            return loadEntity;
        }
        operation = getEntity().getOperation(getActionName());
        if (operation == null) {
            getActionErrors().add(UNDEFINED_OPERATION);
            return ERROR;
        }
        if (getInstanceId() != null && !getInstanceId().trim().equalsIgnoreCase("")) {
            setObject(getEntity().getDao().get(getInstanceId()));
            if (hasObject()) {
                setInstance(new EntityInstance(getInstanceId(), getEntity(), getOperation(), getObject()));
            }
        }
        if (requireObject && !hasObject()) {
            getActionErrors().add(UNDEFINED_OBJECT);
            return ERROR;
        }
        if (hasObject()) {
            this.itemOperations = getEntity().getOperationsFor(getObject(), getOperation(), OperationScope.ITEM);
        }
        this.generalOperations = getEntity().getOperationsFor(null, getOperation(), OperationScope.GENERAL);
        this.selectedOperations = getEntity().getOperationsFor(null, getOperation(), OperationScope.SELECTED);
        return SUCCESS;
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

    protected void processFields() throws PMException {
        for (Map.Entry<String, Object> entry : getInstance().getValues().entrySet()) {
            final String newValue = getStringParameter("field_" + entry.getKey());
            final Field field = getEntity().getFieldById(entry.getKey());
            preConversion();
            try {
                final Converter converter = field.getConverter(getOperation());
                final Object convertedValue = converter.build(field, getObject(), newValue);
                final List<FieldValidator> validators = field.getValidators(getOperation());
                for (FieldValidator fieldValidator : validators) {
                    final Message msg = fieldValidator.validate(getObject(), convertedValue);
                    if (msg != null) {
                        addFieldMsg(field, msg);
                    }
                }
                JPMUtils.set(getObject(), field.getProperty(), convertedValue);
            } catch (IgnoreConvertionException e) {
            } catch (ConverterException e) {
                addFieldMsg(field, e.getMsg());
            }
        }
        if (!getFieldMessages().isEmpty()) {
            throw new ValidationException(null);
        }
    }

    public void preConversion() throws PMException {
        if (getOperation().getContext() != null) {
            getOperation().getContext().preConversion(this);
        }
    }

    public void preExecute() throws PMException {
        if (getOperation().getValidator() != null) {
            getOperation().getValidator().validate(getObject());
        }
        if (getOperation().getContext() != null) {
            getOperation().getContext().preExecute(this);
        }
    }

    public void postExecute() throws PMException {
        if (getOperation().getContext() != null) {
            getOperation().getContext().postExecute(this);
        }
    }

    @Override
    public List<Operation> getGeneralOperations() {
        return generalOperations;
    }

    @Override
    public List<Operation> getSelectedOperations() {
        return selectedOperations;
    }

    public Operation getOperation() {
        return operation;
    }

    public EntityInstance getInstance() {
        return instance;
    }

    public void setInstance(EntityInstance instance) {
        this.instance = instance;
    }

    @Override
    public List<Operation> getItemOperations() {
        return itemOperations;
    }

    public void setItemOperations(List<Operation> itemOperations) {
        this.itemOperations = itemOperations;
    }

    @Override
    public Object getObject() {
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

    public boolean hasObject() {
        return getObject() != null;
    }

    protected GenericDAO getDao() {
        return getEntity().getDao();
    }
}
