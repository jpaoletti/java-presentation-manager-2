package jpaoletti.jpm2.web.converter;

import java.io.Serializable;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.NotAuthorizedException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
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
    @Autowired
    private JPMContext context;

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
        final Object value = getValue(object, field);

        final String res = "@page:show-object-converter.jsp"
                + "?entityId=" + getEntity().getId()
                + "&fields=" + getFields()
                + "&objectId=" + getContext().getEntity().getDao().getId(object);
        if (value == null) {
            return res;
        } else {
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
            return res
                    + "&value=" + value.toString()
                    + "&instanceId=" + localId
                    + "&operationId=" + getOperation()
                    + "&operationLink=" + operationLink
                    + "&operationTitle=" + operationTitle;
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
}
