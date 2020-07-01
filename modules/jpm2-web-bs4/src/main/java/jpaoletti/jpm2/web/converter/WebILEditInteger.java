package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 * In-Line Integer edit.
 *
 * @author jpaoletti
 */
public class WebILEditInteger extends WebEditInteger {

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Integer fieldValue = (Integer) getValue(object, field);
        final String value = (fieldValue == null) ? field.getDefaultValue() : fieldValue.toString();
        return "<a href='javascript:;' class='inline-edit' data-name='" + field.getId() + "' data-type='text' data-align='" + field.getAlign() + "'>" + value + "</a>";
    }
}
