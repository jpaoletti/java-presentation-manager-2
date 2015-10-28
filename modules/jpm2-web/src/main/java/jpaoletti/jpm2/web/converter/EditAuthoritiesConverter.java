package jpaoletti.jpm2.web.converter;

import java.util.Arrays;
import java.util.List;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class EditAuthoritiesConverter extends Converter {

    private boolean readonly = false;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final List<String> value = (List<String>) ((object == null) ? null : getValue(object, field));
        final StringBuilder sb = new StringBuilder("@page:editAuthorities-converter.jsp?readonly=" + isReadonly());
        sb.append("&group=").append(instanceId);
        if (value != null && !value.isEmpty()) {
            sb.append("&value=").append(StringUtils.join(value, ","));
        }
        return sb.toString();
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                final List<String> c = (List<String>) getValue(object, field);
                c.clear();
                final String[] split = ((newValue instanceof String) ? (newValue.toString().split("[,]")) : (String[]) newValue);
                c.addAll(Arrays.asList(split));
                return c;
            } catch (Exception ex) {
                JPMUtils.getLogger().error("error.converting.collection", ex);
                throw new ConverterException("error.converting.collection");
            }
        }
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
