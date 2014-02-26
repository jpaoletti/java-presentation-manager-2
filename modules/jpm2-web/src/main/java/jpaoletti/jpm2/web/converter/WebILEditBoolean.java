package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;

/**
 * In-Line Integer edit.
 *
 * @author jpaoletti
 */
public class WebILEditBoolean extends WebEditBoolean {

    private String trueIcon = "glyphicon-ok";
    private String falseIcon = "glyphicon-remove";

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Boolean fieldValue = (Boolean) getValue(object, field);
        final Boolean value = (fieldValue == null) ? Boolean.valueOf(field.getDefaultValue()) : fieldValue;
        return "<a href='javascript:;' data-field-name='" + field.getId() + "' data-id='" + instanceId + "' data-true-icon='glyphicon " + getTrueIcon() + "' data-false-icon='glyphicon " + getFalseIcon() + "' class='inline-boolean' data-align='" + field.getAlign() + "'><span class='glyphicon " + (value ? getTrueIcon() : getFalseIcon()) + "'></span></a>";
    }

    public String getTrueIcon() {
        return trueIcon;
    }

    public void setTrueIcon(String trueIcon) {
        this.trueIcon = trueIcon;
    }

    public String getFalseIcon() {
        return falseIcon;
    }

    public void setFalseIcon(String falseIcon) {
        this.falseIcon = falseIcon;
    }

}
