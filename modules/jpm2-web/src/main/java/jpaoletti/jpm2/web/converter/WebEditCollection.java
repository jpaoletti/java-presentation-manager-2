package jpaoletti.jpm2.web.converter;

import java.util.Collection;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class WebEditCollection extends WebEditObject {

    private Entity baseEntity;

    public WebEditCollection() {
        super();
    }

    @Override
    public Object visualize(Field field, Object object) throws ConverterException {
        final Collection<Object> value = (Collection<Object>) ((object == null) ? null : getValue(object, field));
        final String res = "@page:collection-converter.jsp?" + getEntity().getId() + "&textField=" + getTextField() + "&pageSize=" + getPageSize() + "&minSearch=" + getMinSearch();
        if (value == null || value.isEmpty()) {
            return res;
        } else {
            final StringBuilder sb = new StringBuilder();
            for (Object o : value) {
                sb.append(getEntity().getDao().getId(o)).append("|");
            }
            return res + "&value=" + sb.toString();
        }
    }

    @Override
    public Object build(Field field, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            final Class clazz = field.getClass(getBaseEntity().getClazz());
            try {
                final Collection c = (Collection) clazz.newInstance();
                final String[] split = ((String) newValue).split("[,]");
                for (String s : split) {
                    c.add(getEntity().getDao().get(s));
                }
                return c;
            } catch (Exception ex) {
                JPMUtils.getLogger().error(ex);
                throw new ConverterException(MessageFactory.error("cannot.create.collection", clazz.getName()));
            }
        }
    }

    public Entity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(Entity baseEntity) {
        this.baseEntity = baseEntity;
    }
}
