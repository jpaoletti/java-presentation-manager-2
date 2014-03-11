package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 * Shows a link that uses the jSON show to get some extra info. Use only with
 * fields that use simple html converters (no pages)
 *
 * @author jpaoletti
 */
public class ShowLinkObject extends ShowObject {

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);

        final String res = "@page:link-object-converter.jsp"
                + "?entityId=" + getEntity().getId()
                + (object != null ? "&objectId=" + getEntity().getDao(getContext().getEntityContext()).getId(value) : "");
        if (value == null) {
            return res;
        } else {
            try {
                final String finalValue = getFinalValue(value);
                return res
                        + "&value=" + finalValue
                        + "&operationId=" + getOperation();
            } catch (ConfigurationException ex) {
                throw new ConverterException(ex.getMessage());
            }
        }
    }
}
