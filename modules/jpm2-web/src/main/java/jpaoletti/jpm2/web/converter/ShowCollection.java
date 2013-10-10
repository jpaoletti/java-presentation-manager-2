package jpaoletti.jpm2.web.converter;

import java.util.Collection;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class ShowCollection extends Converter {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Collection<Object> value = (Collection<Object>) ((object == null) ? null : getValue(object, field));
        if (value == null || value.isEmpty()) {
            return "";
        } else {
            final StringBuilder sb = new StringBuilder("<ul class='show-collection-converter'>");
            for (Object o : value) {
                sb.append("<li>");
                sb.append(o != null ? o : "-");
                sb.append("</li>");
            }
            return sb.append("</ul>").toString();
        }
    }
}
