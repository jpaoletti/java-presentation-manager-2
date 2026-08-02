package jpaoletti.jpm2.core.entityparam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import jpaoletti.jpm2.core.sysparam.SysparamType;
import org.apache.commons.lang3.StringUtils;

/**
 * Code-declared definition of a managed entity parameter: the single source of truth for a parameter's type,
 * default, group, validation and flags — the same idea as {@code SysparamDef} but <b>scoped by a
 * {@code kind}</b> (which parameterized entity it belongs to, e.g. {@code "gateway"}). Applications declare
 * these as {@code public static final} constants and expose them through an {@link EntityParameterModule}.
 *
 * <pre>
 * public static final EntityParameterDef&lt;String&gt; GW_PASSWORD =
 *     EntityParameterDef.secret("gateway", "password").group("credentials").build();
 * </pre>
 *
 * @param <T> the parameter's value type
 * @author jpaoletti
 */
public final class EntityParameterDef<T> {

    private final String kind;
    private final String key;
    private final SysparamType type;
    private final T defaultValue;
    private final String defaultRaw;
    private final String group;
    private final String descriptionKey;
    private final boolean required;
    private final String regex;
    private final Double min;
    private final Double max;
    private final List<String> allowedValues;
    private final boolean deprecated;

    private EntityParameterDef(Builder<T> b) {
        this.kind = b.kind;
        this.key = b.key;
        this.type = b.type;
        this.defaultValue = b.defaultValue;
        this.defaultRaw = b.defaultRaw;
        this.group = b.group;
        this.descriptionKey = b.descriptionKey;
        this.required = b.required;
        this.regex = b.regex;
        this.min = b.min;
        this.max = b.max;
        this.allowedValues = Collections.unmodifiableList(new ArrayList<>(b.allowedValues));
        this.deprecated = b.deprecated;
    }

    public String getKind() {
        return kind;
    }

    public String getKey() {
        return key;
    }

    public SysparamType getType() {
        return type;
    }

    public T getDefault() {
        return defaultValue;
    }

    public String getDefaultRaw() {
        if (defaultRaw != null) {
            return defaultRaw;
        }
        return defaultValue == null ? null : type.format(defaultValue);
    }

    public String getGroup() {
        return group;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    /** @return true if this is a secret parameter, i.e. its type is {@link SysparamType#SECRET}. */
    public boolean isSecret() {
        return type == SysparamType.SECRET;
    }

    public boolean isRequired() {
        return required;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @SuppressWarnings("unchecked")
    public T parse(String raw) {
        return (T) type.parse(raw);
    }

    /**
     * @return null when {@code raw} is valid for this definition, or a human-readable error describing the
     * first violated constraint.
     */
    public String validate(String raw) {
        if (StringUtils.isBlank(raw)) {
            return required ? "Value is required" : null;
        }
        final String typeError = type.validate(raw);
        if (typeError != null) {
            return typeError;
        }
        if (regex != null && !raw.trim().matches(regex)) {
            return "Value does not match the expected format";
        }
        if (!allowedValues.isEmpty() && !allowedValues.contains(raw.trim())) {
            return "Value must be one of: " + String.join(", ", allowedValues);
        }
        if (min != null || max != null) {
            try {
                final double n = new BigDecimal(raw.trim()).doubleValue();
                if (min != null && n < min) {
                    return "Value must be >= " + min;
                }
                if (max != null && n > max) {
                    return "Value must be <= " + max;
                }
            } catch (NumberFormatException e) {
                return "Value must be numeric";
            }
        }
        return null;
    }

    // --- Static factories (one per common value type); all take (kind, key) ---

    public static Builder<String> string(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.STRING);
    }

    public static Builder<Integer> integer(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.INTEGER);
    }

    public static Builder<Long> longParam(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.LONG);
    }

    public static Builder<BigDecimal> decimal(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.DECIMAL);
    }

    public static Builder<Double> doubleParam(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.DOUBLE);
    }

    public static Builder<Boolean> bool(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.BOOLEAN);
    }

    public static Builder<Date> date(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.DATE);
    }

    public static Builder<Date> datetime(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.DATETIME);
    }

    public static Builder<Long> duration(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.DURATION);
    }

    public static Builder<List<String>> list(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.LIST);
    }

    public static Builder<String> json(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.JSON);
    }

    public static Builder<String> url(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.URL);
    }

    public static Builder<String> path(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.PATH);
    }

    /** A string enum constrained to {@code allowed} values. */
    public static Builder<String> enumOf(String kind, String key, String... allowed) {
        return new Builder<String>(kind, key, SysparamType.ENUM).allowed(allowed);
    }

    /** A secret string (encrypted at rest, masked in the UI). */
    public static Builder<String> secret(String kind, String key) {
        return new Builder<>(kind, key, SysparamType.SECRET);
    }

    /**
     * Fluent builder for {@link EntityParameterDef}.
     *
     * @param <T> value type
     */
    public static final class Builder<T> {

        private final String kind;
        private final String key;
        private final SysparamType type;
        private T defaultValue;
        private String defaultRaw;
        private String group = "general";
        private String descriptionKey;
        private boolean required;
        private String regex;
        private Double min;
        private Double max;
        private final List<String> allowedValues = new ArrayList<>();
        private boolean deprecated;

        private Builder(String kind, String key, SysparamType type) {
            this.kind = kind;
            this.key = key;
            this.type = type;
        }

        public Builder<T> def(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Sets the default from its raw string form (as read from source, e.g. the second argument of a
         * {@code getParameter("x", DEFAULT)} call). It is returned verbatim by {@link #getDefaultRaw()}
         * without typed conversion, so it composes with any type without construction/parsing risk.
         */
        public Builder<T> defRaw(String defaultRaw) {
            this.defaultRaw = defaultRaw;
            return this;
        }

        public Builder<T> group(String group) {
            this.group = group;
            return this;
        }

        public Builder<T> description(String descriptionKey) {
            this.descriptionKey = descriptionKey;
            return this;
        }

        public Builder<T> required() {
            this.required = true;
            return this;
        }

        public Builder<T> regex(String regex) {
            this.regex = regex;
            return this;
        }

        public Builder<T> range(double min, double max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder<T> min(double min) {
            this.min = min;
            return this;
        }

        public Builder<T> max(double max) {
            this.max = max;
            return this;
        }

        public Builder<T> allowed(String... values) {
            this.allowedValues.addAll(Arrays.asList(values));
            return this;
        }

        public Builder<T> deprecated() {
            this.deprecated = true;
            return this;
        }

        public EntityParameterDef<T> build() {
            return new EntityParameterDef<>(this);
        }
    }
}
