package jpaoletti.jpm2.core.validator;

import java.util.regex.Pattern;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jpaoletti
 */
public class ValidMail implements FieldValidator {

    private String message = "jpm.validator.mail.invalid";
    private String regexPattern = "^[A-Za-z]+[a-zA-Z0-9._-]+[a-zA-Z0-9]@[A-Za-z]{2,}\\.[A-Za-z]{2,4}$";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final String mail = convertedValue.toString();
        if (StringUtils.isNotEmpty(mail)) {
            Pattern pattern = Pattern.compile(regexPattern);
            if (pattern.matcher(mail).find()) {
                return null;
            } else {
                return MessageFactory.error(getMessage());
            }
        } else {
            return null;
        }
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
