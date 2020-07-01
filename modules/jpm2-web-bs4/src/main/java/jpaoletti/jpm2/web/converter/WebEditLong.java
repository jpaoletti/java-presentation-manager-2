package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditLong extends WebToString {

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Long fieldValue = (Long) getValue(object, field);
        final String value = (fieldValue == null) ? field.getDefaultValue() : fieldValue.toString();
        return "<input class='form-control' name='field_" + field.getId() + "' type='number' value='" + (value != null ? value : "") + "'>";
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                return Long.parseLong(String.valueOf(newValue));
            } catch (NumberFormatException e) {
                throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.long.format", newValue.toString()));
            }
        }
    }
}
