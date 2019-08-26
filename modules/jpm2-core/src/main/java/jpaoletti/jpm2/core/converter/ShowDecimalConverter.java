package jpaoletti.jpm2.core.converter;

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
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object value, String instanceId) throws ConverterException, ConfigurationException {
        if (value == null) {
            return process("");
        } else {
            final DecimalFormat df = new DecimalFormat(getFormat());
            return process(df.format(((Number) value).doubleValue()));
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
