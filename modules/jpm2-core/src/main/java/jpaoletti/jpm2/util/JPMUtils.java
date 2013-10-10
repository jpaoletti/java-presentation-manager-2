package jpaoletti.jpm2.util;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.logging.Logger;

/**
 * Utility helpers
 *
 * @author jpaoletti
 */
public class JPMUtils {

    public static final String LOGGER_NAME = "jpaoletti.jpm2";

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
            throw new ConfigurationException("Property not found: " + propertyName);
        } catch (Exception e) {
            getLogger().error(e);
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
}
