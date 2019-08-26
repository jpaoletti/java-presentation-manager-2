package jpaoletti.jpm2.core.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * properties is something like "{prop1} ... {prop2}" where each prop is a
 * property of the field value.
 *
 * @author jpaoletti
 */
public class ToStringConverter extends Converter {

    public static final Pattern DISPLAY_PATTERN = Pattern.compile("\\{.*?\\}");
    private Integer padCount = 0;
    private String prefix;
    private String suffix;
    private Character padChar = ' ';
    private PadDirection padDirection = PadDirection.LEFT;
    private Integer cutOff;
    private String properties;
    private String nullValue = "-";

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object value, String instanceId) throws ConverterException, ConfigurationException {
        return process(value);
    }

    protected String process(final Object value) {
        String res = (value == null) ? getNullValue() : getFinalValue(value, getProperties());
        if (getCutOff() != null && res.length() > getCutOff()) {
            res = res.substring(0, getCutOff()) + "...";
        }
        switch (getPadDirection()) {
            case LEFT:
                res = JPMUtils.padleft(res, getPadCount(), getPadChar());
                break;
            case RIGHT:
                res = JPMUtils.padright(res, getPadCount(), getPadChar());
                break;
        }
        if (getPrefix() != null) {
            res = getPrefix() + res;
        }
        if (getSuffix() != null) {
            res = res + getSuffix();
        }
        return res;
    }

    public static String getFinalValue(Object value, String properties) {
        String finalValue;
        if (properties != null) {
            final Matcher matcher = DISPLAY_PATTERN.matcher(properties);
            finalValue = properties;
            while (matcher.find()) {
                final String property = matcher.group();
                final String _display = property.replaceAll("\\{", "").replaceAll("\\}", "");
                try {
                    finalValue = finalValue.replace(property, String.valueOf(JPMUtils.get(value, _display)));
                } catch (ConfigurationException ex) {
                    finalValue = finalValue.replace("{" + _display + "}", "?");
                }
            }
        } else {
            finalValue = String.valueOf(value);
        }
        return finalValue;
    }

    public Integer getPadCount() {
        return padCount;
    }

    public void setPadCount(Integer padCount) {
        this.padCount = padCount;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String sufix) {
        this.suffix = sufix;
    }

    public Character getPadChar() {
        return padChar;
    }

    public void setPadChar(Character padChar) {
        this.padChar = padChar;
    }

    public PadDirection getPadDirection() {
        return padDirection;
    }

    public void setPadDirection(PadDirection padDirection) {
        this.padDirection = padDirection;
    }

    public Integer getCutOff() {
        return cutOff;
    }

    public void setCutOff(Integer cutOff) {
        this.cutOff = cutOff;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getNullValue() {
        return nullValue;
    }

    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    public static enum PadDirection {

        LEFT, RIGHT
    }
}
