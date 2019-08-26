package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.ToStringConverter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebToString extends ToStringConverter {

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object value, String instanceId) throws ConverterException, ConfigurationException {
        return wrap(field, process(value), value);
    }

    public String wrap(Field field, String process, Object value) {
        final String originalValue = (value == null) ? getNullValue() : getFinalValue(value, getProperties());
        return "<span class='to-string' title='" + originalValue + "' data-align='" + field.getAlign() + "'>" + process + "</span>";
    }
}
