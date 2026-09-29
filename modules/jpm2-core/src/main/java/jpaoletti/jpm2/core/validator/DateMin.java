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
public class DateMin implements FieldValidator {

    private String min; //yyyy-MM-dd or current day if null
    private String message = "jpm.validator.too.small";

    @Override
    public Message validate(Object object, Object convertedValue) {
        if (!(convertedValue instanceof Date)) {
            return null;
        }
        try {
            final Date value = (Date) convertedValue;
            // min is yyyy-MM-dd (see the property); null means now
            final Date maxdate = min == null ? new Date() : new SimpleDateFormat("yyyy-MM-dd").parse(getMin());
            if (value.after(maxdate)) {
                return null;
            } else {
                return MessageFactory.error(getMessage(), String.valueOf(convertedValue), String.valueOf(getMin()));
            }
        } catch (ParseException ex) {
            return MessageFactory.error("Invalid max format", min);
        }
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
