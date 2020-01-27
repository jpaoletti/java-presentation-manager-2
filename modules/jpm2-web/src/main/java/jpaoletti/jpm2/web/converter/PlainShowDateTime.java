package jpaoletti.jpm2.web.converter;

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
public class WebShowDateTime extends WebToString {

    private String format = "yyyy-MM-dd";

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Date value = (Date) object;
        if (value != null) {
            final DateTime dt = new DateTime(value);
            return wrap(field, process(dt.toString(getFormat())), value);
        } else {
            return "-";
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
