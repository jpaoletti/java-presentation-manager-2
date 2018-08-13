package jpaoletti.jpm2.web.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author jpaoletti
 */
public class WebEditDecimal extends WebToString {

    private String format = "#0.00";
    private Character decimalSeparator = '.';
    private Character groupingSeparator = ',';
    private String min = "0.00";
    private String max = "999999999.99";
    private String moreOptions = "";

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final BigDecimal fieldValue = (BigDecimal) getValue(object, field);
        final String value = (fieldValue == null) ? field.getDefaultValue() : getFormater().format(fieldValue);
        return "@page:decimal-converter.jsp?value=" + value + "&options=" + getOptions();
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                final String val = (String) newValue;
                return new BigDecimal(getFormater().parse(val).doubleValue());
            } catch (NumberFormatException | ParseException e) {
                throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.decimal.format", newValue.toString()));
            }
        }
    }

    public DecimalFormat getFormater() {
        final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator(getDecimalSeparator());
        otherSymbols.setGroupingSeparator(getGroupingSeparator());
        return new DecimalFormat(getFormat(), otherSymbols);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getOptions() {
        return String.format("{aSep: '%s', aDec: '%s', vMin: '%s', vMax: '%s' %s}", getGroupingSeparator(), getDecimalSeparator(), getMin(), getMax(), StringUtils.isEmpty(getMoreOptions()) ? "" : "," + getMoreOptions());
    }

    public Character getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(Character decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public Character getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(Character groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMoreOptions() {
        return moreOptions;
    }

    public void setMoreOptions(String moreOptions) {
        this.moreOptions = moreOptions;
    }
}
