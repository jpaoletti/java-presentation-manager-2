package jpaoletti.jpm2.core.converter;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class ToStringConverter extends Converter {

    private Integer padCount = 0;
    private String prefix;
    private String suffix;
    private Character padChar = ' ';
    private PadDirection padDirection = PadDirection.LEFT;
    private Integer cutOff;

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field);
        return process(value);
    }

    protected String process(final Object value) {
        String res = value != null ? String.valueOf(value) : "";
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

    public static enum PadDirection {

        LEFT, RIGHT
    }
}
