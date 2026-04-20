package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Tag;

/**
 * Converter to display a single Tag instance as a styled tag badge.
 *
 * @author jpaoletti
 */
public class ShowTag extends Converter {

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Tag value = (Tag) ((object == null) ? null : object);
        if (value == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder("<div class='show-tags-converter'>");
        sb.append("<span data-id='").append(value.getId()).append("' style='").append(value.getStyle() != null ? value.getStyle() : "").append("' class='tag'>");
        sb.append(value.getText());
        sb.append("</span>");
        return sb.append("</div>").toString();
    }
}
