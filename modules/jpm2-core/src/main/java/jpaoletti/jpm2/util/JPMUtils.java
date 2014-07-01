package jpaoletti.jpm2.util;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.logging.Logger;
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
        return Logger.getLogger(LOGGER_NAME);
    }

    /**
     * Getter for an object property value
     *
     * @param obj The object
     * @param propertyName The property
     * @return The value of the property of the object
     *
     */
    public static Object get(Object obj, String propertyName) throws ConfigurationException {
        try {
            if (obj != null && propertyName != null) {
                return PropertyUtils.getNestedProperty(obj, propertyName);
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
}
