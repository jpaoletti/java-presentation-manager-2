package jpaoletti.jpm2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import org.json.JSONException;
import org.json.JSONObject;
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

    /**
     * Logging categories. Each one is a child logger of {@link #LOGGER_NAME},
     * so they can be enabled/disabled independently from log4j (e.g.
     * {@code <Logger name="jpaoletti.jpm2.service" level="debug"/>}) while the
     * parent {@code jpaoletti.jpm2} logger still controls all of them at once.
     */
    public static final String SERVICE = LOGGER_NAME + ".service";
    public static final String CONTROLLER = LOGGER_NAME + ".controller";
    public static final String DAO = LOGGER_NAME + ".dao";
    public static final String MODEL = LOGGER_NAME + ".model";
    public static final String CONVERTER = LOGGER_NAME + ".converter";
    public static final String AUTH = LOGGER_NAME + ".auth";
    public static final String WEB = LOGGER_NAME + ".web";

    /**
     * Parameter keys whose value must never be logged in clear text. Matched
     * case-insensitively as a substring of the key.
     */
    private static final String[] SENSITIVE_KEYS = {"password", "pass", "secret", "token", "csrf"};

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
     * Returns the logger for the given category. Use the category constants
     * declared in this class ({@link #SERVICE}, {@link #CONTROLLER},
     * {@link #DAO}, {@link #MODEL}, {@link #CONVERTER}, {@link #AUTH},
     * {@link #WEB}) so each subsystem can be tuned independently from log4j.
     *
     * @param category the logger name (typically one of the category constants)
     * @return the log4j logger for that category
     */
    public static Logger getLogger(String category) {
        return LogManager.getLogger(category);
    }

    /**
     * Renders a request parameter map for debug logging, masking the values of
     * sensitive keys (see {@link #SENSITIVE_KEYS}). Each entry is shown as
     * {@code key=[v1, v2]}. Intended to be called only behind an
     * {@code isDebugEnabled()} guard.
     *
     * @param parameters the raw request parameters (may be null)
     * @return a single-line, safe-to-log representation
     */
    public static String formatParams(Map<String, ?> parameters) {
        if (parameters == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(entry.getKey()).append("=");
            if (isSensitive(entry.getKey())) {
                sb.append("[***]");
            } else {
                sb.append(formatParamValue(entry.getValue()));
            }
        }
        return sb.append("}").toString();
    }

    private static String formatParamValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Object[]) {
            return java.util.Arrays.deepToString((Object[]) value);
        }
        if (value instanceof int[]) {
            return java.util.Arrays.toString((int[]) value);
        }
        if (value instanceof long[]) {
            return java.util.Arrays.toString((long[]) value);
        }
        if (value instanceof double[]) {
            return java.util.Arrays.toString((double[]) value);
        }
        if (value instanceof float[]) {
            return java.util.Arrays.toString((float[]) value);
        }
        if (value instanceof boolean[]) {
            return java.util.Arrays.toString((boolean[]) value);
        }
        if (value instanceof byte[]) {
            return java.util.Arrays.toString((byte[]) value);
        }
        if (value instanceof short[]) {
            return java.util.Arrays.toString((short[]) value);
        }
        if (value instanceof char[]) {
            return java.util.Arrays.toString((char[]) value);
        }
        return String.valueOf(value);
    }

    /**
     * Masks the given value if the key denotes sensitive data, otherwise
     * returns it unchanged. Useful for single field name/value pairs (e.g.
     * inline editing).
     *
     * @param key the parameter/field name
     * @param value the value
     * @return the value, or {@code ***} when the key is sensitive
     */
    public static String maskValue(String key, String value) {
        return isSensitive(key) ? "***" : value;
    }

    private static boolean isSensitive(String key) {
        if (key == null) {
            return false;
        }
        final String lower = key.toLowerCase();
        for (String s : SENSITIVE_KEYS) {
            if (lower.contains(s)) {
                return true;
            }
        }
        return false;
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
        for (Field field : entity.getAllFields(null)) {
            try {
                originalValues.put(field.getId(), snapshotValue(Converter.getValue(object, field)));
            } catch (ConfigurationException ex) {
                originalValues.put(field.getId(), null);
            }
        }
        return originalValues;
    }

    /**
     * Returns a defensive snapshot of the given value, suitable for later
     * comparison against a possibly mutated version of the same property.
     * Collections and maps are shallow-copied so that in-place mutations on
     * the original instance (e.g. clear/add cycles done by collection
     * converters) do not silently invalidate the snapshot.
     */
    private static Object snapshotValue(Object value) {
        if (value instanceof Set) {
            return new LinkedHashSet<>((Set<?>) value);
        }
        if (value instanceof Collection) {
            return new ArrayList<>((Collection<?>) value);
        }
        if (value instanceof Map) {
            return new LinkedHashMap<>((Map<?, ?>) value);
        }
        return value;
    }

    /**
     * Equality check tailored for detailed audit comparisons. Differs from
     * {@link Objects#equals} in three ways: BigDecimals are compared by value
     * ignoring scale, a null value is treated as equivalent to an empty
     * collection/map, and collections are compared by content (Sets ignore
     * order, other collections preserve order).
     */
    public static boolean auditEquals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null) {
            return isEmptyContainer(b);
        }
        if (b == null) {
            return isEmptyContainer(a);
        }
        if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).compareTo((BigDecimal) b) == 0;
        }
        if (a instanceof Set && b instanceof Set) {
            return a.equals(b);
        }
        if (a instanceof Collection && b instanceof Collection) {
            final Collection<?> ca = (Collection<?>) a;
            final Collection<?> cb = (Collection<?>) b;
            if (ca.size() != cb.size()) {
                return false;
            }
            return new ArrayList<>(ca).equals(new ArrayList<>(cb));
        }
        return Objects.equals(a, b);
    }

    private static boolean isEmptyContainer(Object o) {
        if (o instanceof Collection) {
            return ((Collection<?>) o).isEmpty();
        }
        if (o instanceof Map) {
            return ((Map<?, ?>) o).isEmpty();
        }
        return false;
    }

    /**
     * Renders an audit diff for the given pair of values. For collections,
     * shows added and removed items; for everything else, shows the classic
     * "old -&gt; new" form.
     */
    public static String formatAuditDiff(Object original, Object current) {
        if (original instanceof Collection || current instanceof Collection) {
            final Collection<?> oc = (original instanceof Collection) ? (Collection<?>) original : java.util.Collections.emptyList();
            final Collection<?> nc = (current instanceof Collection) ? (Collection<?>) current : java.util.Collections.emptyList();
            final List<Object> added = new ArrayList<>();
            final List<Object> removed = new ArrayList<>();
            final Set<Object> originalSet = new HashSet<>(oc);
            final Set<Object> currentSet = new HashSet<>(nc);
            for (Object o : nc) {
                if (!originalSet.contains(o)) {
                    added.add(o);
                }
            }
            for (Object o : oc) {
                if (!currentSet.contains(o)) {
                    removed.add(o);
                }
            }
            final StringBuilder sb = new StringBuilder();
            if (!added.isEmpty()) {
                sb.append("+").append(added);
            }
            if (!removed.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append("-").append(removed);
            }
            if (sb.length() == 0) {
                sb.append(Objects.toString(original)).append(" -&gt; ").append(Objects.toString(current));
            }
            return sb.toString();
        }
        return Objects.toString(original) + " -&gt; " + Objects.toString(current);
    }

    /**
     * Builds the detailed audit diff for an updated object: iterates the entity
     * fields, compares each current value against the pre-update snapshot in
     * {@code originalValues} and renders the changed ones as
     * {@code <b>Field</b>: old -> new<br/>}. Returns an empty string when
     * nothing changed.
     */
    public static String buildAuditDiff(Entity entity, Object object, Map<String, Object> originalValues) {
        final StringBuilder sb = new StringBuilder();
        for (Field field : entity.getAllFields(null)) {
            Object newValue;
            try {
                newValue = Converter.getValue(object, field);
            } catch (ConfigurationException ex) {
                newValue = null;
            }
            final Object original = originalValues.get(field.getId());
            if (!auditEquals(original, newValue)) {
                sb.append(String.format("<b>%s</b>: %s",
                        field.getTitle(entity), formatAuditDiff(original, newValue))).append("<br/>");
            }
        }
        return sb.toString();
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

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
    }

    public static String readJsonAsStringFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return jsonText;
        }
    }
}
