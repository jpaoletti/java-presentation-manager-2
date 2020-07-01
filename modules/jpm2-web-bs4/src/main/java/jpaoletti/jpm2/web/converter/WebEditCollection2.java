package jpaoletti.jpm2.web.converter;

import java.util.Collection;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class WebEditCollection2 extends WebEditObject {

    public WebEditCollection2() {
        super();
    }

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Collection<Object> value = (Collection<Object>) ((object == null) ? null : getValue(object, field));
        final String res = getBaseJsp()
                + "?entityId=" + getEntity().getId()
                + "&textField=" + getTextField()
                + "&related=" + getRelated()
                + "&filter=" + ((getFilter() != null) ? getFilter().getId() : "")
                + "&pageSize=" + getPageSize()
                + "&minSearch=" + getMinSearch();
        if (value == null || value.isEmpty()) {
            return res;
        } else {
            final StringBuilder sb = new StringBuilder();
            for (Object o : value) {
                sb.append(getEntity().getDao().getId(o)).append(",");
            }
            return res + "&value=" + sb.toString().substring(0, sb.toString().length() - 1);
        }
    }

    protected String getBaseJsp() {
        return "@page:collection-converter2.jsp";
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                final Collection<Object> c = (Collection<Object>) getValue(object, field);
                c.clear();
                final String[] split = splitValues(newValue);
                for (String s : split) {
                    c.add(getEntity().getDao().get(s));
                }
                return c;
            } catch (Exception ex) {
                JPMUtils.getLogger().error("", ex);
                throw new ConverterException("error.converting.collection");
            }
        }
    }

    protected String[] splitValues(Object newValue) {
        return (newValue instanceof String) ? (new String[]{newValue.toString()}) : (String[]) newValue;
    }
}
