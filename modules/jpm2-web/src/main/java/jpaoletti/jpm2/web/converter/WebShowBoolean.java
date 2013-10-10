package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebShowBoolean extends WebToString {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Boolean value = (Boolean) getValue(object, field);
        if (value == null || value) {
            return process("<span class=\"glyphicon glyphicon-ok\"></span>");
        } else {
            return process("<span class=\"glyphicon glyphicon-remove\"></span>");
        }
    }
}
