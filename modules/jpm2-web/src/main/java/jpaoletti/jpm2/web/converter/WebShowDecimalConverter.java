package jpaoletti.jpm2.web.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebShowDecimalConverter extends WebToString {

    private String format = "#0.00";

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final BigDecimal value = (BigDecimal) getValue(object, field);
        if (value == null) {
            return wrap(field, process(""));
        } else {
            final DecimalFormat df = new DecimalFormat(getFormat());
            return wrap(field, process(df.format(value.doubleValue())));
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
