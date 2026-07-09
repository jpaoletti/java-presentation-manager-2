package jpaoletti.jpm2.core.mail;

import java.util.Map;

/**
 * Supported mail sender types. Each type knows how to build its concrete
 * {@link GeneralMailSender} from a parameter map.
 *
 * @author mmusicco
 */
public enum MailSenderType {

    SMTP {
        @Override
        public GeneralMailSender build(Map<String, String> parameters) {
            return new SMTPMailSender(parameters);
        }
    };

    public abstract GeneralMailSender build(Map<String, String> parameters);
}
