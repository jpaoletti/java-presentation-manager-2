package jpaoletti.jpm2.web.converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditDecimal extends WebToString {

    private String options = "{aSep: ',', aDec: '.'}"; //autoNumeric options in jSON format

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final BigDecimal fieldValue = (BigDecimal) getValue(object, field);
        final String value = (fieldValue == null) ? field.getDefaultValue() : fieldValue.toString();
        return "@page:decimal-converter.jsp?value=" + value + "&options=" + getOptions();
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                final String val = (String) newValue;
                return new BigDecimal(NumberFormat.getNumberInstance(Locale.ROOT).parse(val).doubleValue());
            } catch (NumberFormatException | ParseException e) {
                throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.decimal.format", newValue.toString()));
            }
        }
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

}
