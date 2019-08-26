package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebShowBoolean extends WebToString {

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object value, String instanceId) throws ConverterException, ConfigurationException {
        if (value == null || (Boolean) value) {
            return process("<span class=\"glyphicon glyphicon-ok\"></span>");
        } else {
            return process("<span class=\"glyphicon glyphicon-remove\"></span>");
        }
    }
}
