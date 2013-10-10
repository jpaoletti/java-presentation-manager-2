package jpaoletti.jpm2.core.converter;

import jpaoletti.jpm2.core.exception.ConverterException;
import java.util.Date;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.model.Field;
import org.joda.time.DateTime;

/**
 *
 * @author jpaoletti
 */
public class ShowDateTimeConverter extends ToStringConverter {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Date value = (Date) getValue(object, field);
        final DateTime dt = new DateTime(value);
        return process(dt.toString(getConfig("format", "yyyy-MM-dd")));
    }
}
