package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditString extends WebToString {

    @Override
    public Object visualize(Field field, Object object) throws ConverterException {
        final String value = (object == null) ? field.getDefaultValue() : (String) getValue(object, field);
        return "<input class='form-control' name='field_" + field.getId() + "' type='text' value='" + (value != null ? value : "") + "'>";
    }

    @Override
    public Object build(Field field, Object newValue) throws ConverterException {
        return newValue;
    }
}
