package jpaoletti.jpm2.core.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class DateMax implements FieldValidator {

    private String max; //yyyy-MM-dd or current day if null
    private String message = "jpm.validator.too.big";

    @Override
    public Message validate(Object object, Object convertedValue) {
        if (!(convertedValue instanceof Date)) {
            return null;
        }
        try {
            final Date value = (Date) convertedValue;
            // max is yyyy-MM-dd (see the property); null means now
            final Date maxdate = max == null ? new Date() : new SimpleDateFormat("yyyy-MM-dd").parse(getMax());
            if (value.before(maxdate)) {
                return null;
            } else {
                return MessageFactory.error(getMessage(), String.valueOf(convertedValue), String.valueOf(getMax()));
            }
        } catch (ParseException ex) {
            return MessageFactory.error("Invalid max format", max);
        }
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
