package jpaoletti.jpm2.core.converter;

import java.util.Date;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.joda.time.DateTime;

/**
 *
 * @author jpaoletti
 */
public class ShowDateTimeConverter extends ToStringConverter {

    private String format = "yyyy-MM-dd";

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object value, String instanceId) throws ConverterException, ConfigurationException {
        final DateTime dt = new DateTime((Date) value);
        return process(dt.toString(getFormat()));
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
