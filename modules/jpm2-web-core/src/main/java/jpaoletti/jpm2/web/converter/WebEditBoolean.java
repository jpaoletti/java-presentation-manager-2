package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditBoolean extends WebToString {

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object value, String instanceId) throws ConverterException, ConfigurationException {
        return "<input name='field_" + field.getId() + "' type=\"checkbox\" " + ((value == null || (Boolean) value) ? "checked" : "") + "> ";
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        return newValue != null && !"".equalsIgnoreCase(((String) newValue).trim());
    }
}
