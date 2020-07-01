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

    private String trueIcon = "fa fa-check-circle";
    private String falseIcon = "fas fa-times";

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Boolean value = (Boolean) getValue(object, field);
        if (value == null || value) {
            return process("<span class=\"" + getTrueIcon() + "\"></span>");
        } else {
            return process("<span class=\"" + getFalseIcon() + "\"></span>");
        }
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
