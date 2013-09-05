package jpaoletti.jpm2.web.converter;

import java.math.BigDecimal;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditDecimal extends WebToString {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
        final BigDecimal fieldValue = (BigDecimal) getValue(object, field);
        final String value = (fieldValue == null) ? field.getDefaultValue() : fieldValue.toString();
        return "<input class='form-control' step='any' name='field_" + field.getId() + "' type='number' value='" + (value != null ? value : "") + "'>";
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                return new BigDecimal((String) newValue);
            } catch (NumberFormatException e) {
                throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.decimal.format", newValue.toString()));
            }
        }
    }
}
