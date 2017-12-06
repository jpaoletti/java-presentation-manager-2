package jpaoletti.jpm2.core.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class PlainConverter extends Converter {

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }
}
