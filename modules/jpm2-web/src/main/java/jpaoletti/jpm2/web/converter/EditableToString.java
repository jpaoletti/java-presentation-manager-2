package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class EditableToString extends WebToString {

    @Override
    public String wrap(Field field, String value) {
        return "<a href='javascript:;' class='inline-edit' data-name='" + field.getId() + "' data-type='text' data-align='" + field.getAlign() + "'>" + value + "</a>";
    }
}
