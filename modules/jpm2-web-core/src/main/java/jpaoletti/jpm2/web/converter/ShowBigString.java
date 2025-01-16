package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class ShowBigString extends Converter {

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        return "<textarea class='form-control' disabled>" + (object != null ? object : "") + "</textarea>";
    }
}
