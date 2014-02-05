package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class EditableToString extends WebToString {

    @Override
    public String wrap(Field field, String processed, Object value) {
        final String originalValue = (value == null) ? getNullValue() : getFinalValue(value, getProperties());
        return "<a href='javascript:;' class='inline-edit' title='" + originalValue + "' data-name='" + field.getId() + "' data-type='text' data-align='" + field.getAlign() + "'>" + value + "</a>";
    }
}
