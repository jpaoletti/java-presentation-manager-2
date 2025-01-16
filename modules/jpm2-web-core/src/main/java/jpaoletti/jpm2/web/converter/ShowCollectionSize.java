package jpaoletti.jpm2.web.converter;

import java.util.Collection;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class ShowCollectionSize extends Converter {

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Collection<Object> value = (Collection<Object>) ((object == null) ? null : object);
        return "<span class='to-string' data-align='" + field.getAlign() + "'>" + (value == null ? 0 : value.size()) + "</span>";
    }
}
