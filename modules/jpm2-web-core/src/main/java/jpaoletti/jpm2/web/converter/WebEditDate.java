package jpaoletti.jpm2.web.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class WebEditDate extends WebToString {

    public static final String RFC3339 = "yyyy-MM-dd";

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Date date = (Date) getValue(object, field);
        String value = field.getDefaultValue();
        if (date != null) {
            value = new SimpleDateFormat(RFC3339).format(date);
        }
        return "<input class='form-control' name='field_" + field.getId() + "' type='date' value='" + value + "'>";
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            final SimpleDateFormat sdf = new SimpleDateFormat(RFC3339);
            try {
                return sdf.parse(newValue.toString());
            } catch (ParseException ex) {
                throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.date.format", newValue.toString()));
            }
        }
    }
}
