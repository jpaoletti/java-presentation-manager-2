package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class EditableToString extends WebToString {

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);
        String processed = process(value);
        if (getAuthorizationService().userHasRole("jpm.auth.operation." + contextualEntity + ".edit")) {
            final String originalValue = (value == null) ? "" : value.toString();
            final String finalValue = (value == null) ? getNullValue() : getFinalValue(value, getProperties());
            return "<a href=\"javascript:;\" class=\"inline-edit\" title=\"" + originalValue + "\" data-name=\"" + field.getId() + "\" data-type=\"text\" data-align=\"" + field.getAlign() + "\">" + finalValue + "</a>";
        } else {
            return wrap(field, processed, value);
        }
    }
}
