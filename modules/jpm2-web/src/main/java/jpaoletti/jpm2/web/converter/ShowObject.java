package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Shows a link that uses the jSON show to get some extra info. Use only with
 * fields that use simple html converters (no pages)
 *
 * @author jpaoletti
 */
public class ShowObject extends Converter {

    private Entity entity;
    private String fields;
    @Autowired
    private JPMContext context;

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
        final Object value = getValue(object, field);
        final String res = "@page:show-object-converter.jsp?entityId=" + getEntity().getId() + "&fields=" + getFields() + "&objectId=" + getContext().getEntity().getDao().getId(object);
        if (value == null) {
            return res;
        } else {
            return res + "&value=" + value.toString() + "&instanceId=" + getEntity().getDao().getId(value);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
