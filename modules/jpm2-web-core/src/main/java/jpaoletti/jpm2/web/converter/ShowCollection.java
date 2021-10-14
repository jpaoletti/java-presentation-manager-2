package jpaoletti.jpm2.web.converter;

import java.util.Collection;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class ShowCollection extends ShowObject {

    private Entity entity;
    private String textField;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);
        return visualizeValue(contextualEntity, field, value, instanceId);
    }

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Collection<Object> value = (Collection<Object>) ((object == null) ? null : object);
        if (value == null || value.isEmpty()) {
            return "";
        } else {
            final StringBuilder sb = new StringBuilder("<ul class='show-collection-converter'>");
            for (Object o : value) {
                sb.append("<li>");
                sb.append(o != null ? getFinalValue(o) : "-");
                sb.append("</li>");
            }
            return sb.append("</ul>").toString();
        }
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public String getTextField() {
        return textField;
    }

    @Override
    public void setTextField(String textField) {
        this.textField = textField;
    }
}
