package jpaoletti.jpm2.core.converter;

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
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object value, String instanceId) throws ConverterException, ConfigurationException {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }
}
