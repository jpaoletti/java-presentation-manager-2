package jpaoletti.jpm2.web.converter;

import java.io.Serializable;
import java.util.regex.Matcher;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.FieldNotFoundException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.controller.ListController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Shows a link that uses the jSON show to get some extra info. Use only with
 * fields that use simple html converters (no pages)
 *
 * @author jpaoletti
 */
public class ShowObject extends Converter {

    private Entity entity;
    private String fields;
    private String operation;
    private String textField;
    private Integer cutOff;
    @Autowired
    private JPMContext context;

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);

        final String res = "@page:show-object-converter.jsp"
                + "?entityId=" + getEntity().getId()
                + "&fields=" + getFields()
                + (object != null ? "&objectId=" + getContext().getEntity().getDao().getId(object) : "");
        if (value == null) {
            return res;
        } else {
            try {
                final Serializable localId = getEntity().getDao().getId(value);
                String operationLink = "";
                String operationTitle = "";
                if (getOperation() != null) {
                    final Operation op = getEntity().getOperation(getOperation());
                    if (op != null) {
                        try {
                            op.checkAuthorization();
                        } catch (NotAuthorizedException ex) {
                        }
                        operationTitle = getMessage(op.getTitle(), getMessage(getEntity().getTitle()));
                        switch (op.getScope()) {
                            case ITEM:
                                operationLink = "jpm/" + getEntity().getId() + "/" + localId + "/" + getOperation();
                                break;
                            default:
                                operationLink = "jpm/" + getEntity().getId() + "/" + getOperation();
                                break;
                        }
                    } else {
                        throw new ConverterException(MessageFactory.error("jpm.converter.operation.not.exists", getEntity().getId(), getOperation()));
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
            } catch (NotAuthorizedException ex) {
                throw new ConverterException("Not authorized.");
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

    private String getFinalValue(Object value) throws FieldNotFoundException, ConfigurationException {
        String finalValue;
        if (getTextField() != null) {
            if (!getTextField().contains("{")) {
                final Field field = getEntity().getFieldById(getTextField());
                return String.valueOf(JPMUtils.get(value, field.getProperty()));
            } else {
                final Matcher matcher = ListController.DISPLAY_PATTERN.matcher(textField);
                finalValue = textField;
                while (matcher.find()) {
                    final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                    final Field field2 = entity.getFieldById(_display_field);
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
}
