package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditBoolean extends WebToString {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Boolean value = (Boolean) getValue(object, field);
        return "<input name='field_" + field.getId() + "' type=\"checkbox\" " + ((value == null || value) ? "checked" : "") + "> ";
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
        return newValue != null && !"".equalsIgnoreCase(((String) newValue).trim());
    }
}
