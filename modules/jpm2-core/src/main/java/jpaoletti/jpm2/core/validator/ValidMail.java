package jpaoletti.jpm2.core.validator;

import java.util.regex.Pattern;
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
    private String regexPattern = "^[A-Za-z0-9]+[a-zA-Z0-9._-]+[a-zA-Z0-9]@([A-Za-z0-9_-]{1,}\\.)+([A-Za-z]){2,}$";
    private boolean useRegex = false;

    @Override
    public Message validate(Object object, Object convertedValue) {
        final String mail = convertedValue.toString();
        if (StringUtils.isNotEmpty(mail)) {
            if (isUseRegex()) {
                Pattern pattern = Pattern.compile(regexPattern);
                if (pattern.matcher(mail).find()) {
                    return null;
                } else {
                    return MessageFactory.error(getMessage());
                }
            } else {
                if (EmailValidator.getInstance().isValid(mail)) {
                    return null;
                } else {
                    return MessageFactory.error(getMessage());
                }
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

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public boolean isUseRegex() {
        return useRegex;
    }

    public void setUseRegex(boolean useRegex) {
        this.useRegex = useRegex;
    }

}
