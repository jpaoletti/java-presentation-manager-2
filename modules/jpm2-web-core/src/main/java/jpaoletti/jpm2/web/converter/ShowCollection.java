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
    private boolean linked = false;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);
        return visualizeValue(contextualEntity, field, value, instanceId);
    }

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Collection<Object> value = (Collection<Object>) ((object == null) ? null : object);
        final StringBuilder sb = new StringBuilder("<ul class='show-collection-converter " + (isLinked() ? "show-collection-converter-linked" : "") + "'>");
        if (value != null) {
            for (Object o : value) {
                sb.append("<li>");
                if (isLinked()) {
                    sb.append(o != null ? "<a href='@cp@/jpm/" + getEntity().getId() + "/" + getEntity().getDao().getId(o) + "/show.exec'>" + getFinalValue(o) + "</a>" : "-");
                } else {
                    sb.append(o != null ? getFinalValue(o) : "-");
                }
                sb.append("</li>");
            }
        }
        return sb.append("</ul>").toString();
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

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }
}
