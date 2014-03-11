package jpaoletti.jpm2.core.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class ShowDecimalConverter extends ToStringConverter {

    private String format = "#0.00";

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final BigDecimal value = (BigDecimal) getValue(object, field);
        if (value == null) {
            return process("");
        } else {
            final DecimalFormat df = new DecimalFormat(getFormat());
            return process(df.format(value.doubleValue()));
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
