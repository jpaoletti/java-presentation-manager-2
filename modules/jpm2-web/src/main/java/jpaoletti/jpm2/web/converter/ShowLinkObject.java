package jpaoletti.jpm2.web.converter;

import java.util.logging.Level;
import java.util.logging.Logger;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;

/**
 * Shows a link that uses the jSON show to get some extra info. Use only with
 * fields that use simple html converters (no pages)
 *
 * @author jpaoletti
 */
public class ShowLinkObject extends ShowObject {

    private String ctx;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);

        final String res = "@page:link-object-converter.jsp"
                + "?extraClass=" + getExtraClass(contextualEntity, field, object, instanceId)
                + "&entityId=" + getEntity().getId()
                + ((getCtx() != null) ? "&ctx=" + getCtx() : "")
                + (object != null ? "&objectId=" + getEntity().getDao(getCtx()).getId(value) : "");
        if (value == null) {
            return res;
        } else {
            try {
                final String finalValue = getFinalValue(value);
                final Operation op = getEntity().getOperation(getOperation(), getContext().getContext());
                return res
                        + "&value=" + finalValue
                        + "&operationId=" + op.getPathId();
            } catch (ConfigurationException | NotAuthorizedException ex) {
                throw new ConverterException(ex.getMessage());
            }
        }
    }

    public String getCtx() {
        return ctx;
    }

    public void setCtx(String ctx) {
        this.ctx = ctx;
    }

    //overrideable
    public String getExtraClass(ContextualEntity contextualEntity, Field field, Object object, String instanceId) {
        return "";
    }

}
