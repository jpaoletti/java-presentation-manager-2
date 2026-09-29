package jpaoletti.jpm2.web.converter;

import static jpaoletti.jpm2.core.converter.ToStringConverter.getFinalValue;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class EditableToString extends WebToString {

    private boolean always = false;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object value, String instanceId) throws ConverterException, ConfigurationException {
        String processed = escape(process(value), value);
        if (always || getAuthorizationService().userHasRole("jpm.auth.operation." + contextualEntity + ".edit")) {
            final String originalValue = (value == null) ? "" : value.toString();
            final String finalValue = (value == null) ? getNullValue() : getFinalValue(value, getProperties());
            return "<a href=\"javascript:;\" class=\"inline-edit\" title=\"" + escapeAttribute(originalValue, value) + "\" data-name=\"" + field.getId() + "\" data-type=\"text\" data-align=\"" + field.getAlign() + "\">" + escape(finalValue, value) + "</a>";
        } else {
            return wrap(field, processed, value);
        }
    }

    public boolean isAlways() {
        return always;
    }

    public void setAlways(boolean always) {
        this.always = always;
    }

}
