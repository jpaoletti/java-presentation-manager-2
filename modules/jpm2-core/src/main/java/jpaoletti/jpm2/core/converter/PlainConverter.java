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

    public static final String PAGE_PREFIX = "@page:";

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object value, String instanceId) throws ConverterException, ConfigurationException {
        if (value == null) {
            return "";
        } else if (value instanceof String && ((String) value).startsWith(PAGE_PREFIX)) {
            // A value can't be taken as a page include: prepend a zero width space
            return "\u200B" + value;
        } else {
            return value;
        }
    }
}
