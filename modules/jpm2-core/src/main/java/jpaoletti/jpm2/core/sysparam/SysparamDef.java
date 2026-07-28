package jpaoletti.jpm2.core.sysparam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Code-declared definition of a managed sysparam: the single source of truth for a
 * parameter's type, default, group, validation and flags. Applications declare these
 * as {@code public static final} constants (so each carries its own value type
 * {@code T}) and expose them through a {@link SysparamModule} for catalog registration
 * and auto-seeding.
 *
 * <pre>
 * public static final SysparamDef&lt;Integer&gt; SMTP_PORT =
 *     SysparamDef.integer("mail.smtp.port").def(25).group("mail").range(1, 65535).build();
 * </pre>
 *
 * @param <T> the parameter's value type
 * @author jpaoletti
 */
public final class SysparamDef<T> {

    private final String key;
    private final SysparamType type;
    private final T defaultValue;
    private final String group;
    private final String descriptionKey;
    private final boolean cached;
    private final boolean required;
    private final String regex;
    private final Double min;
    private final Double max;
    private final List<String> allowedValues;
    private final boolean deprecated;
    private final String sinceVersion;

    private SysparamDef(Builder<T> b) {
        this.key = b.key;
        this.type = b.type;
        this.defaultValue = b.defaultValue;
        this.group = b.group;
        this.descriptionKey = b.descriptionKey;
        this.cached = b.cached;
        this.required = b.required;
        this.regex = b.regex;
        this.min = b.min;
        this.max = b.max;
        this.allowedValues = Collections.unmodifiableList(new ArrayList<>(b.allowedValues));
        this.deprecated = b.deprecated;
        this.sinceVersion = b.sinceVersion;
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

    public boolean isCached() {
        return cached;
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

    public String getSinceVersion() {
        return sinceVersion;
    }

    /**
     * Parses a stored raw value into its typed representation. Throws on malformed input.
     */
    @SuppressWarnings("unchecked")
    public T parse(String raw) {
        return (T) type.parse(raw);
    }

    /**
     * @return null when the raw value is valid for this definition, or a human-readable
     * error message describing the first violated constraint.
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

    // --- Static factories (one per common value type) ---

    public static Builder<String> string(String key) {
        return new Builder<>(key, SysparamType.STRING);
    }

    public static Builder<Integer> integer(String key) {
        return new Builder<>(key, SysparamType.INTEGER);
    }

    public static Builder<Long> longParam(String key) {
        return new Builder<>(key, SysparamType.LONG);
    }

    public static Builder<BigDecimal> decimal(String key) {
        return new Builder<>(key, SysparamType.DECIMAL);
    }

    public static Builder<Double> doubleParam(String key) {
        return new Builder<>(key, SysparamType.DOUBLE);
    }

    public static Builder<Boolean> bool(String key) {
        return new Builder<>(key, SysparamType.BOOLEAN);
    }

    public static Builder<Date> date(String key) {
        return new Builder<>(key, SysparamType.DATE);
    }

    public static Builder<Date> datetime(String key) {
        return new Builder<>(key, SysparamType.DATETIME);
    }

    public static Builder<Long> duration(String key) {
        return new Builder<>(key, SysparamType.DURATION);
    }

    public static Builder<List<String>> list(String key) {
        return new Builder<>(key, SysparamType.LIST);
    }

    public static Builder<String> json(String key) {
        return new Builder<>(key, SysparamType.JSON);
    }

    public static Builder<String> url(String key) {
        return new Builder<>(key, SysparamType.URL);
    }

    public static Builder<String> path(String key) {
        return new Builder<>(key, SysparamType.PATH);
    }

    /** A string enum constrained to {@code allowed} values. */
    public static Builder<String> enumOf(String key, String... allowed) {
        return new Builder<String>(key, SysparamType.ENUM).allowed(allowed);
    }

    /** A secret string (encrypted at rest, masked in the UI). */
    public static Builder<String> secret(String key) {
        return new Builder<>(key, SysparamType.SECRET);
    }

    /**
     * Fluent builder for {@link SysparamDef}.
     *
     * @param <T> value type
     */
    public static final class Builder<T> {

        private final String key;
        private final SysparamType type;
        private T defaultValue;
        private String group = "general";
        private String descriptionKey;
        private boolean cached = true;
        private boolean required;
        private String regex;
        private Double min;
        private Double max;
        private final List<String> allowedValues = new ArrayList<>();
        private boolean deprecated;
        private String sinceVersion;

        private Builder(String key, SysparamType type) {
            this.key = key;
            this.type = type;
        }

        public Builder<T> def(T defaultValue) {
            this.defaultValue = defaultValue;
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

        public Builder<T> cached(boolean cached) {
            this.cached = cached;
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

        public Builder<T> since(String version) {
            this.sinceVersion = version;
            return this;
        }

        public SysparamDef<T> build() {
            return new SysparamDef<>(this);
        }
    }
}
