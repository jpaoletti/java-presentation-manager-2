package jpaoletti.jpm2.core.message;

import java.io.Serializable;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * This class represents any message that system want to send to the user. It
 * may be system wide, entity o field scoped, an error, warning or information
 * message.
 *
 * @author jpaoletti
 * @since 13/09/2011
 * @version v1.1
 *
 */
public class Message implements Serializable {

    private MessageType type;
    private String key;
    private String text;
    private String[] args;

    public Message(MessageType type, String key, String[] args) {
        this.type = type;
        this.key = key;
        this.args = args;
        build();
    }

    public boolean isError() {
        return getType().equals(MessageType.ERROR);
    }

    public boolean isWarn() {
        return getType().equals(MessageType.WARN);
    }

    public boolean isInfo() {
        return getType().equals(MessageType.INFO);
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getArguments() {
        if (getArgs().length > 0) {
            final StringBuilder sb = new StringBuilder();
            for (String string : getArgs()) {
                sb.append(string).append(";");
            }
            return sb.toString().substring(0, sb.toString().length() - 1);
        } else {
            return "";
        }
    }

    /**
     * The arguments escaped for html, to render the message in a page: the
     * message pattern (which may contain entities like &amp;aacute;) is trusted,
     * the arguments (usually user input) are not.
     */
    public String[] getHtmlEscapedArgs() {
        final String[] args = getArgs();
        if (args == null) {
            return new String[0];
        }
        final String[] res = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            res[i] = args[i] == null ? null : StringEscapeUtils.escapeHtml4(args[i]);
        }
        return res;
    }

    public final void build() {
        try {
            this.text = getMessageSource().getMessage(getKey(), getArgs(), LocaleContextHolder.getLocale());
        } catch (Exception e) {
            this.text = getKey();
        }

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private MessageSource getMessageSource() {
        return (MessageSource) JPMUtils.getApplicationContext().getBean("messageSource");
    }
}
