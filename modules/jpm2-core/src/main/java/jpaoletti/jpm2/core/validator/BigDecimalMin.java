package jpaoletti.jpm2.core.validator;

import java.math.BigDecimal;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class BigDecimalMin implements FieldValidator {

    private BigDecimal min;
    private String message = "jpm.validator.too.small";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final BigDecimal value = (BigDecimal) convertedValue;
        if (value == null || (getMin() != null && value.compareTo(getMin()) >= 0)) {
            return null;
        } else {
            return MessageFactory.error(getMessage(), String.valueOf(convertedValue), String.valueOf(getMin()));
        }
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
