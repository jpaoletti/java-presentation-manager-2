package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.converter.ToStringConverter;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebToString extends ToStringConverter {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
        final Object value = getValue(object, field);
        return wrap(field, process(value));
    }

    public String wrap(Field field, String process) {
        return "<span class='to-string' data-align='" + field.getAlign() + "'>" + process + "</span>";
    }
}
