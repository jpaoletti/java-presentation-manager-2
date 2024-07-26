package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author jpaoletti
 */
public class ValidMail implements FieldValidator {

    private String message = "jpm.validator.mail.invalid";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final String mail = convertedValue.toString();
        if (StringUtils.isNotEmpty(mail)) {
            if (EmailValidator.getInstance().isValid(mail)) {
                return null;
            } else {
                return MessageFactory.error(getMessage());
            }
        } else {
            return null;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
