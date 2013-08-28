package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditInteger extends WebToString {

    @Override
    public Object visualize(Field field, Object object) throws ConverterException {
        final Integer fieldValue = (Integer) getValue(object, field);
        final String value = (fieldValue == null) ? field.getDefaultValue() : fieldValue.toString();
        return "<input class='form-control' name='field_" + field.getId() + "' type='number' value='" + (value != null ? value : "") + "'>";
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                return Integer.parseInt((String) newValue);
            } catch (NumberFormatException e) {
                throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.int.format", newValue.toString()));
            }
        }
    }
}
