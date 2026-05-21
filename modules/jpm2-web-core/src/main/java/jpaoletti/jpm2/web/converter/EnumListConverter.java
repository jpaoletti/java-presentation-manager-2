package jpaoletti.jpm2.web.converter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
public class EnumListConverter extends WebToString {

    @Autowired
    private JPMContext context;

    private Class clazz;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        try {
            final Collection value = (Collection) ((object == null) ? null : object);
            final List<String> options = new ArrayList<>();
            for (Object option : EnumSet.allOf(getClazz())) {
                options.add(((Enum) option).name() + "@" + String.valueOf(option) + "@" + (value != null && value.contains(option) ? "1" : "0"));
            }
            final StringBuilder sb = new StringBuilder("@page:enum-list-converter.jsp?options=" + URLEncoder.encode(StringUtils.join(options, ","), "UTF-8"));
            return sb.toString();
        } catch (UnsupportedEncodingException ignoreme) {
            return "";
        }
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException, ConfigurationException {
        Collection c = (Collection) getValue(object, field);
        if (c == null) {
            c = EnumSet.noneOf(getClazz());
        }
        c.clear();
        if (newValue != null && !"".equals(newValue)) {
            final String[] values = ((newValue instanceof String) ? (new String[]{newValue.toString()}) : (String[]) newValue);
            for (String value : values) {
                c.add(valueOf(getClazz(), value));
            }
        }
        return c;
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    protected Enum valueOf(final Class enumClass, Object newValue) {
        return Enum.valueOf(enumClass, String.valueOf(newValue));
    }
}
