package jpaoletti.jpm2.core.mail;

import java.util.Map;

/**
 * SMTP implementation of {@link GeneralMailSender}. Builds a {@link MailConfig}
 * from the parameter map and sends asynchronously through a {@link MailManager}.
 *
 * @author jpaoletti
 */
public class SMTPMailSender extends GeneralMailSender {

    public SMTPMailSender(Map<String, String> parameters) {
        super(parameters);
    }

    @Override
    public void send(Mail mail) {
        final MailManager mgr = new MailManager(getParameter("subject-prefix", ""), getMailConfig());
        mgr.sendAsync(mail);
    }

    @Override
    public void sendSync(Mail mail) throws Exception {
        final MailManager mgr = new MailManager(getParameter("subject-prefix", ""), getMailConfig());
        mgr.send(mail);
    }

    public MailConfig getMailConfig() {
        final MailConfig config = new MailConfig();
        config.setFromName(getParameter("from-name", ""));
        config.setFrom(getParameter("from", ""));
        config.setUser(getParameter("user", ""));
        config.setPassword(getParameter("password", ""));
        config.setHost(getParameter("host", "smtp.gmail.com"));
        config.setPort(getParameter("port", "587"));
        config.setDebug(getParameter("debug", true));
        config.setAuth(getParameter("auth", true));
        config.setSsl(getParameter("ssl", false));
        config.setTlsEnabled(getParameter("tls", "true"));
        return config;
    }
}
