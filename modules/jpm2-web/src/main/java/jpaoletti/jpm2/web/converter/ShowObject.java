package jpaoletti.jpm2.web.converter;

import java.io.Serializable;
import java.util.regex.Matcher;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.converter.Converter;
import static jpaoletti.jpm2.core.converter.ToStringConverter.DISPLAY_PATTERN;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.FieldNotFoundException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Shows a link that uses the jSON show to get some extra info. Use only with
 * fields that use simple html converters (no pages)
 *
 * @author jpaoletti
 */
public class ShowObject extends Converter {

    private Entity entity;
    private String entityContext;
    private String fields;
    private String operation;
    private String operationAuth;
    private String textField;
    private Integer cutOff;
    @Autowired
    private JPMContext context;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);

        final String res = "@page:show-object-converter.jsp"
                + "?entityId=" + getEntity().getId()
                + "&fields=" + getFields()
                + (object != null ? "&objectId=" + getContext().getEntity().getDao(getContext().getEntityContext()).getId(object) : "");
        if (value == null) {
            return res;
        } else {
            try {
                final Serializable localId = getEntity().getDao(getContext().getEntityContext()).getId(value);
                String operationLink = "";
                String operationTitle = "";
                if (getOperation() != null && (getOperationAuth() == null || getAuthorizationService().userHasRole(getOperationAuth()))) {
                    try {
                        final Operation op = getEntity().getOperation(getOperation(), getContext().getContext());
                        operationTitle = getMessage(op.getTitle(), getMessage(getEntity().getTitle()));
                        final String entityId = getEntity().getId() + ((getEntityContext() == null) ? "" : (PresentationManager.CONTEXT_SEPARATOR + getEntityContext()));
                        switch (op.getScope()) {
                            case ITEM:
                                operationLink = "jpm/" + entityId + "/" + localId + "/" + getOperation();
                                break;
                            default:
                                operationLink = "jpm/" + entityId + "/" + getOperation();
                                break;
                        }
                    } catch (NotAuthorizedException | OperationNotFoundException ex) {
                        //We don't care for now
                    }
                }
                final String finalValue = getFinalValue(value);
                return res
                        + "&value=" + finalValue
                        + "&instanceId=" + localId
                        + "&operationId=" + getOperation()
                        + "&operationLink=" + operationLink
                        + "&operationTitle=" + operationTitle;
            } catch (ConfigurationException ex) {
                throw new ConverterException(ex.getMessage());
            }
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    protected String getFinalValue(Object value) throws FieldNotFoundException, ConfigurationException {
        String finalValue;
        if (getTextField() != null) {
            if (!getTextField().contains("{")) {
                final Field field = getEntity().getFieldById(getTextField(), getContext().getEntityContext());
                return String.valueOf(JPMUtils.get(value, field.getProperty()));
            } else {
                final Matcher matcher = DISPLAY_PATTERN.matcher(textField);
                finalValue = textField;
                while (matcher.find()) {
                    final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                    final Field field2 = entity.getFieldById(_display_field, getContext().getEntityContext());
                    finalValue = finalValue.replace("{" + _display_field + "}", String.valueOf(JPMUtils.get(value, field2.getProperty())));
                }
            }
        } else {
            finalValue = String.valueOf(value);

        }
        if (getCutOff() != null && finalValue.length() > getCutOff()) {
            finalValue = finalValue.substring(0, getCutOff()) + "...";
        }
        return finalValue;
    }

    public Integer getCutOff() {
        return cutOff;
    }

    public void setCutOff(Integer cutOff) {
        this.cutOff = cutOff;
    }

    public String getOperationAuth() {
        return operationAuth;
    }

    public void setOperationAuth(String operationAuth) {
        this.operationAuth = operationAuth;
    }

    public String getEntityContext() {
        return entityContext;
    }

    public void setEntityContext(String entityContext) {
        this.entityContext = entityContext;
    }
}
