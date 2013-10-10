package jpaoletti.jpm2.web.converter;

import java.util.Date;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;
import org.joda.time.DateTime;

/**
 *
 * @author jpaoletti
 */
public class WebShowDateTime extends WebToString {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Date value = (Date) getValue(object, field);
        if (value != null) {
            final DateTime dt = new DateTime(value);
            return wrap(field, process(dt.toString(getConfig("format", "yyyy-MM-dd"))));
        } else {
            return "-";
        }
    }
}
