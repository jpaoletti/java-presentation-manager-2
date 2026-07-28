package jpaoletti.jpm2.core.sysparam;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Value type of a sysparam. Centralizes parsing (stored String -&gt; typed object),
 * formatting (typed object -&gt; stored String) and validation, so both typed reads
 * and save-time validation share one source of truth.
 *
 * <p>Values are always stored as text; the type drives interpretation. Parsing throws
 * on invalid input; {@link #validate(String)} returns a human-readable error (or null).
 *
 * @author jpaoletti
 */
public enum SysparamType {

    STRING,
    INTEGER,
    LONG,
    DECIMAL,
    DOUBLE,
    BOOLEAN,
    DATE,
    DATETIME,
    /** Duration expressed as a number of milliseconds. */
    DURATION,
    /** Free string constrained to an allowed-values list on the definition. */
    ENUM,
    JSON,
    /** Comma-separated list of strings. */
    LIST,
    URL,
    EMAIL,
    /** Filesystem path (stored verbatim, light validation only). */
    PATH,
    /**
     * Opaque secret string: stored encrypted at rest and masked in the UI. Secrecy is a
     * property of the type (not a separate mutable flag), so a parameter is secret if and
     * only if its type is SECRET. Parsing/formatting/validation behave like {@link #STRING}.
     */
    SECRET;

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    /**
     * Parses the stored raw value into its typed representation. Returns null for a
     * null input. Throws {@link IllegalArgumentException} on malformed input.
     */
    public Object parse(String raw) {
        if (raw == null) {
            return null;
        }
        final String v = raw.trim();
        try {
            switch (this) {
                case INTEGER:
                    return Integer.valueOf(v);
                case LONG:
                case DURATION:
                    return Long.valueOf(v);
                case DECIMAL:
                    return new BigDecimal(v);
                case DOUBLE:
                    return Double.valueOf(v);
                case BOOLEAN:
                    return parseBoolean(v);
                case DATE:
                    return parseDate(raw, DATE_FORMAT);
                case DATETIME:
                    return parseDate(raw, DATETIME_FORMAT);
                case LIST:
                    return parseList(raw);
                case JSON:
                    return parseJson(raw);
                default:
                    // STRING, ENUM, URL, EMAIL, PATH, SECRET
                    return raw;
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid " + name() + " value: '" + raw + "'", e);
        }
    }

    /**
     * Formats a typed value into its stored representation.
     */
    public String format(Object value) {
        if (value == null) {
            return null;
        }
        switch (this) {
            case DATE:
                return new SimpleDateFormat(DATE_FORMAT).format((Date) value);
            case DATETIME:
                return new SimpleDateFormat(DATETIME_FORMAT).format((Date) value);
            case LIST:
                return String.join(",", ((List<?>) value).stream().map(String::valueOf).toArray(String[]::new));
            default:
                return String.valueOf(value);
        }
    }

    /**
     * @return null if the raw value is valid for this type, or a human-readable error.
     */
    public String validate(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null; // emptiness is handled by the 'required' flag, not by the type
        }
        try {
            parse(raw);
            if (this == URL && !raw.trim().matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.+")) {
                return "Invalid URL";
            }
            if (this == EMAIL && !raw.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                return "Invalid email";
            }
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static Boolean parseBoolean(String v) {
        if (StringUtils.equalsAnyIgnoreCase(v, "true", "yes", "y", "1", "on")) {
            return Boolean.TRUE;
        }
        if (StringUtils.equalsAnyIgnoreCase(v, "false", "no", "n", "0", "off")) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value: '" + v + "'");
    }

    private static Date parseDate(String raw, String pattern) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            return sdf.parse(raw.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date '" + raw + "' (expected " + pattern + ")", e);
        }
    }

    private static List<String> parseList(String raw) {
        final List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(raw)) {
            return result;
        }
        for (String part : raw.split(",")) {
            final String t = part.trim();
            if (!t.isEmpty()) {
                result.add(t);
            }
        }
        return result;
    }

    private static Object parseJson(String raw) {
        final String t = raw.trim();
        if (t.startsWith("[")) {
            return new JSONArray(t);
        }
        return new JSONObject(t);
    }
}
