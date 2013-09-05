package jpaoletti.jpm2.core.converter;

import java.util.Date;
import jpaoletti.jpm2.core.model.Field;
import org.joda.time.DateTime;

/**
 *
 * @author jpaoletti
 */
public class ShowDateTimeConverter extends ToStringConverter {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
        final Date value = (Date) getValue(object, field);
        final DateTime dt = new DateTime(value);
        return process(dt.toString(getConfig("format", "yyyy-MM-dd")));
    }
}
