package jpaoletti.jpm2.web.converter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
public class EnumConverter extends WebToString {

    @Autowired
    private JPMContext context;

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        try {
            final Object value = (object == null) ? null : getValue(object, field);
            final Class enumClass = getEnumClass(field);
            final List<String> options = new ArrayList<>();
            for (Object option : EnumSet.allOf(enumClass)) {
                options.add(((Enum) option).name() + "@" + String.valueOf(option));
            }
            final StringBuilder sb = new StringBuilder("@page:enum-converter.jsp?options=" + URLEncoder.encode(StringUtils.join(options, ","), "UTF-8"));
            if (value != null) {
                sb.append("&value=").append(((Enum) value).name());
            }
            return sb.toString();
        } catch (UnsupportedEncodingException ignoreme) {
            return "";
        }
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            final Class enumClass = getEnumClass(field);
            return valueOf(enumClass, newValue);
        }
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    protected Class getEnumClass(Field field) {
        return field.getClass(getContext().getEntity().getClazz());
    }

    protected Enum valueOf(final Class enumClass, Object newValue) {
        return Enum.valueOf(enumClass, String.valueOf(newValue));
    }
}
