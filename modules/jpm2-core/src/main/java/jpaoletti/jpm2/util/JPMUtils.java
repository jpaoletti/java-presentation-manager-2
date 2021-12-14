package jpaoletti.jpm2.util;

import java.util.LinkedHashMap;
import java.util.Map;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility helpers
 *
 * @author jpaoletti
 */
@Component
public class JPMUtils implements ApplicationContextAware {

    public static final String LOGGER_NAME = "jpaoletti.jpm2";

    private static ApplicationContext context;

    /**
     * Pad to the left
     *
     * @param s string
     * @param len desired len
     * @param c padding char
     * @return padded string
     */
    public static String padleft(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(len);
        int fill = len - s.length();
        while (fill-- > 0) {
            sb.append(c);
        }
        sb.append(s);
        return sb.toString();
    }

    /**
     * Pad to the right
     *
     * @param s string
     * @param len desired len
     * @param c padding char
     * @return padded string
     */
    public static String padright(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(len);
        int fill = len - s.length();
        sb.append(s);
        while (fill-- > 0) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String hexdump(byte[] p) {
        return p.toString();
    }

    public static String zeropad(String s, int i) {
        return padleft(s, i, '0');
    }

    /**
     * Creates a new instance object of the given class.
     *
     * @param clazz The Class of the new Object
     * @return The new Object or null on any error.
     */
    public static Object newInstance(String clazz) {
        try {
            return Class.forName(clazz).newInstance();
        } catch (Exception e) {
            getLogger().error(e);
            return null;
        }
    }

    public static Logger getLogger() {
        return LogManager.getLogger(LOGGER_NAME);
    }

    /**
     * Getter for an object property value
     *
     * @param obj The object
     * @param propertyName The property
     * @return The value of the property of the object
     * @throws jpaoletti.jpm2.core.exception.ConfigurationException
     *
     */
    public static Object get(Object obj, String propertyName) throws ConfigurationException {
        try {
            if (obj != null && propertyName != null) {
                if (obj instanceof Map) {
                    return ((Map) obj).get(propertyName);
                } else {
                    return PropertyUtils.getNestedProperty(obj, propertyName);
                }
            }
        } catch (NullPointerException | NestedNullException e) {
        } catch (NoSuchMethodException e) {
            getLogger().warn("NoSuchMethodException: " + propertyName, e);
            return null;
        } catch (Exception e) {
            getLogger().error("Unexpected error", e);
            return "-undefined-";
        }
        return null;
    }

    /**
     * Setter for an object property value
     *
     * @param obj The object
     * @param name The property name
     * @param value The value to set
     *
     */
    public static void set(Object obj, String name, Object value) {
        try {
            PropertyUtils.setNestedProperty(obj, name, value);
        } catch (Exception e) {
            getLogger().error(e);
        }
    }

    public static Map<String, Object> getOriginalValues(Entity entity, Object object) {
        final Map<String, Object> originalValues = new LinkedHashMap<>();
        for (Field field : entity.getFields()) {
            try {
                originalValues.put(field.getId(), Converter.getValue(object, field));
            } catch (ConfigurationException ex) {
                originalValues.put(field.getId(), null);
            }
        }
        return originalValues;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        JPMUtils.context = context;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity passed for initialization is null");
        }
        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

    public static <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return (T) ((Advised) proxy).getTargetSource().getTarget();
        } else {
            return (T) proxy;
        }
    }
}
