package jpaoletti.jpm2.web.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.joda.time.DateTime;

/**
 *
 * @author jpaoletti
 */
public class WebEditDate2 extends WebToString {

    private String format = "yyyy-MM-dd";

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Date date = (Date) getValue(object, field);
        String value = field.getDefaultValue();
        if (date != null) {
            final DateTime dt = new DateTime(date);
            value = dt.toString(getFormat());
        }
        return "@page:date2-converter.jsp?value=" + value + "&format=" + getFormat();
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            final SimpleDateFormat sdf = new SimpleDateFormat(getFormat());
            try {
                return sdf.parse(newValue.toString());
            } catch (ParseException ex) {
                throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.date.format", newValue.toString()));
            }
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
