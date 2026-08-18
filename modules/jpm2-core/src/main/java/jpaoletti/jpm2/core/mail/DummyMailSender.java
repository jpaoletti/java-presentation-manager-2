package jpaoletti.jpm2.core.mail;

import java.util.Arrays;
import java.util.Map;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Mail sender intended for environments where messages must not leave the
 * application. It logs the message instead of contacting an SMTP server.
 *
 * @author jpaoletti
 */
public class DummyMailSender extends GeneralMailSender {

    public DummyMailSender(Map<String, String> parameters) {
        super(parameters);
    }

    @Override
    public void send(Mail mail) {
        log(mail);
    }

    @Override
    public void sendSync(Mail mail) {
        log(mail);
    }

    private void log(Mail mail) {
        JPMUtils.getLogger().info(
                "DummyMailSender: mail no enviado; to={}, cc={}, cco={}, replyTo={}, subject={}, body={}",
                Arrays.toString(mail.getTo()),
                Arrays.toString(mail.getCc()),
                Arrays.toString(mail.getCco()),
                mail.getReplyTo(),
                mail.getSubject(),
                mail.getBody());
    }
}
