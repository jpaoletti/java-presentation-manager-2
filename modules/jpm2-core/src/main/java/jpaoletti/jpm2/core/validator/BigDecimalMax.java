package jpaoletti.jpm2.core.validator;

import java.math.BigDecimal;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class BigDecimalMax implements FieldValidator {

    private BigDecimal max;
    private String message = "jpm.validator.too.big";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final BigDecimal value = (BigDecimal) convertedValue;
        if (value == null || (getMax() != null && value.compareTo(getMax()) <= 0)) {
            return null;
        } else {
            return MessageFactory.error(getMessage(), String.valueOf(convertedValue), String.valueOf(getMax()));
        }
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
