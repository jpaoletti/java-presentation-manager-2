package jpaoletti.jpm2.core.mail;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Header;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;

//import org.apache.james.jdkim.DKIMSigner;
//import org.apache.james.jdkim.api.BodyHasher;
//import org.apache.james.jdkim.api.Headers;
//import org.apache.james.jdkim.api.SignatureRecord;
/**
 * Manager for sending emails.
 *
 * @author jpaoletti
 * @since 22/11/2011
 * @version 2.0.0
 *
 */
public class MailManager {

    protected String appname;
    private MailConfig config;
    private String privateKey;
    private String pkPassword;

    public MailManager(String appname, MailConfig config, String privateKey, String pkPassword) {
        this.appname = appname;
        if (config != null) {
            this.config = config;
        }
        this.privateKey = privateKey;
        this.pkPassword = pkPassword;
    }

    public MailManager(String appname, MailConfig config) {
        this.appname = appname;
        if (config != null) {
            this.config = config;
        }
    }

    public MailManager(String appname) {
        this.appname = appname;
    }

    /**
     * Generic mail send. Supports signing, plain text and multiple attachments.
     *
     * @param mail
     * @throws jakarta.mail.MessagingException
     */
    public void send(Mail mail) throws MessagingException {
        final String user = getConfig().getUser();
        final Boolean auth = getConfig().isAuth();
        final Session session = getNewSession(user, auth);
        final MimeMessage msg = initBasicMessage(session, mail.getReplyTo(), mail.getTo(), mail.getSubject());
        if (mail.getCc() != null) {
            msg.setRecipients(Message.RecipientType.CC, getAddress(mail.getCc()));
        }
        if (mail.getCco() != null) {
            msg.setRecipients(Message.RecipientType.BCC, getAddress(mail.getCco()));
        }
        if (mail.getAttachs() != null || mail.getTextbody() != null) {
            final Multipart multipart = new MimeMultipart("alternative");
            // HTML version
            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(mail.getBody(), "text/html; charset=UTF-8");

            if (mail.getTextbody() != null) {
                // Unformatted text version
                final MimeBodyPart textPart = new MimeBodyPart();
                textPart.setContent(mail.getTextbody(), "text/plain; charset=UTF-8");
                multipart.addBodyPart(textPart);
            }
            if (mail.getAttachs() != null) {
                for (File file : mail.getAttachs()) {
                    final MimeBodyPart part = new MimeBodyPart();
                    final DataSource source = new FileDataSource(file);
                    part.setDataHandler(new DataHandler(source));
                    part.setFileName(file.getName());
                    multipart.addBodyPart(part);
                }
            }
            multipart.addBodyPart(htmlPart);
            msg.setContent(multipart);
        } else {
            msg.setContent(mail.getBody(), "text/html; charset=UTF-8");
        }

        // Signing
        /*if (mail.getDkimSignTemplate() != null) {
            try {
                final PKCS8Key pkcs8 = new PKCS8Key(new ByteArrayInputStream(getPrivateKey().getBytes()), getPkPassword() != null ? getPkPassword().toCharArray() : null);
                final PrivateKey pk = pkcs8.getPrivateKey();
                final DKIMSigner signer = new DKIMSigner(mail.getDkimSignTemplate(), pk);
                final SignatureRecord signRecord = signer.newSignatureRecordTemplate(mail.getDkimSignTemplate());
                final BodyHasher bhj = signer.newBodyHasher(signRecord);
                final Headers headers = new MimeMessageHeaders(msg);
                try {
                    OutputStream os = new HeaderSkippingOutputStream(bhj.getOutputStream());
                    msg.writeTo(os);
                    bhj.getOutputStream().close();
                } catch (IOException e) {
                    throw new MessagingException("Exception calculating bodyhash: "
                            + e.getMessage(), e);
                }
                final String signatureHeader = signer.sign(headers, bhj);
                prependHeader(msg, signatureHeader);
            } catch (Exception e) {
                throw new MessagingException("General security exception: " + e.getMessage(), e);
            }
        }*/
        sendMsg(session, getConfig(), msg);
    }

    /**
     * Sends a mail with a single attached file.
     */
    public void send(String subject, String body, File attach, String... to) throws MessagingException {
        final String user = getConfig().getUser();
        final Boolean auth = getConfig().isAuth();
        final Session session = getNewSession(user, auth);

        // create a message
        final MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(getConfig().getFrom(), getConfig().getFromName()));
        } catch (UnsupportedEncodingException ex) {
            msg.setFrom(new InternetAddress(getConfig().getFrom()));
        }
        msg.setRecipients(Message.RecipientType.TO, getAddress(to));
        if (getAppname() != null) {
            msg.setSubject("[" + getAppname() + "] " + subject);
        } else {
            msg.setSubject(subject);
        }
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html");
        final Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();
        final DataSource source = new FileDataSource(attach);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(attach.getName());
        multipart.addBodyPart(messageBodyPart);
        msg.setContent(multipart);
        sendMsg(session, getConfig(), msg);
    }

    public void send(String subject, String body, String... to) throws MessagingException {
        sendReplyTo(subject, body, (String) null, to);
    }

    public void sendAsync(String subject, String body, String... to) {
        new AsyncMailSender(this, new Mail(subject, body, to)).start();
    }

    public void sendAsync(Mail mail) {
        new AsyncMailSender(this, mail).start();
    }

    /**
     * Sends a DKIM-signed mail. If the privateKey is encoded using a
     * password then you can pass the password as privateKeyPassword parameter.
     *
     * Sample configuration:
     *
     * <pre><code>
     *   &lt;signatureTemplate&gt;v=1; s=selector; d=example.com; h=from:to:received:received; a=rsa-sha256; bh=; b=;&lt;/signatureTemplate&gt;
     *   &lt;privateKey&gt;
     *   -----BEGIN RSA PRIVATE KEY-----
     *   MIICXAIBAAKBgQDYDaYKXzwVYwqWbLhmuJ66aTAN8wmDR+rfHE8HfnkSOax0oIoT
     *   M5zquZrTLo30870YMfYzxwfB6j/Nz3QdwrUD/t0YMYJiUKyWJnCKfZXHJBJ+yfRH
     *   r7oW+UW3cVo9CG2bBfIxsInwYe175g9UjyntJpWueqdEIo1c2bhv9Mp66QIDAQAB
     *   AoGBAI8XcwnZi0Sq5N89wF+gFNhnREFo3rsJDaCY8iqHdA5DDlnr3abb/yhipw0I
     *   /1HlgC6fIG2oexXOXFWl+USgqRt1kTt9jXhVFExg8mNko2UelAwFtsl8CRjVcYQO
     *   cedeH/WM/mXjg2wUqqZenBmlKlD6vNb70jFJeVaDJ/7n7j8BAkEA9NkH2D4Zgj/I
     *   OAVYccZYH74+VgO0e7VkUjQk9wtJ2j6cGqJ6Pfj0roVIMUWzoBb8YfErR8l6JnVQ
     *   bfy83gJeiQJBAOHk3ow7JjAn8XuOyZx24KcTaYWKUkAQfRWYDFFOYQF4KV9xLSEt
     *   ycY0kjsdxGKDudWcsATllFzXDCQF6DTNIWECQEA52ePwTjKrVnLTfCLEG4OgHKvl
     *   Zud4amthwDyJWoMEH2ChNB2je1N4JLrABOE+hk+OuoKnKAKEjWd8f3Jg/rkCQHj8
     *   mQmogHqYWikgP/FSZl518jV48Tao3iXbqvU9Mo2T6yzYNCCqIoDLFWseNVnCTZ0Q
     *   b+IfiEf1UeZVV5o4J+ECQDatNnS3V9qYUKjj/krNRD/U0+7eh8S2ylLqD3RlSn9K
     *   tYGRMgAtUXtiOEizBH6bd/orzI9V9sw8yBz+ZqIH25Q=
     *   -----END RSA PRIVATE KEY-----
     *   &lt;/privateKey&gt;
     * </code></pre>
     *
     * By default the mailet assume that Javamail will convert LF to CRLF when
     * sending so will compute the hash using converted newlines. If you don't
     * want this behaviout then set forceCRLF attribute to false.
     */
    /* public void signedSend(String subject, String body, String replyTo, String signatureTemplate, String... to) throws MessagingException {
        try {
            final PKCS8Key pkcs8 = new PKCS8Key(new ByteArrayInputStream(getPrivateKey().getBytes()), getPkPassword() != null ? getPkPassword().toCharArray() : null);
            final PrivateKey pk = pkcs8.getPrivateKey();
            final DKIMSigner signer = new DKIMSigner(signatureTemplate, pk);
            final SignatureRecord signRecord = signer.newSignatureRecordTemplate(signatureTemplate);
            final BodyHasher bhj = signer.newBodyHasher(signRecord);
            final String user = getConfig().getUser();
            final Boolean auth = getConfig().isAuth();
            final Session session = getNewSession(user, auth);
            final MimeMessage message = initMessage(session, replyTo, to, subject, body);
            final Headers headers = new MimeMessageHeaders(message);
            try {
                OutputStream os = new HeaderSkippingOutputStream(bhj.getOutputStream());
                message.writeTo(os);
                bhj.getOutputStream().close();
            } catch (IOException e) {
                throw new MessagingException("Exception calculating bodyhash: "
                        + e.getMessage(), e);
            }
            final String signatureHeader = signer.sign(headers, bhj);
            prependHeader(message, signatureHeader);
            sendMsg(session, getConfig(), message);
        } catch (Exception e) {
            throw new MessagingException("PermFail while signing: " + e.getMessage(), e);
        }
    }*/
    /**
     * Sends a mail.
     *
     * @param subject
     * @param body
     * @param replyTo
     * @param to
     * @throws MessagingException
     */
    public void sendReplyTo(String subject, String body, String replyTo, String... to) throws MessagingException {
        final String user = getConfig().getUser();
        final Boolean auth = getConfig().isAuth();
        final Session session = getNewSession(user, auth);
        final MimeMessage msg = initMessage(session, replyTo, to, subject, body);
        sendMsg(session, getConfig(), msg);
    }

    protected InternetAddress[] getAddress(String[] s) throws AddressException {
        if (s == null) {
            throw new AddressException("No se indicaron destinatarios");
        }
        final List<InternetAddress> address = new ArrayList<>(s.length);
        for (final String raw : s) {
            final String clean = raw == null ? null : raw.trim();
            if (StringUtils.isBlank(clean)) {
                JPMUtils.getLogger().warn("Se ignora una direccion de mail vacia");
                continue;
            }
            try {
                address.add(new InternetAddress(clean));
            } catch (AddressException ex) {
                JPMUtils.getLogger().warn("Se ignora una direccion de mail invalida: '" + clean + "'");
            }
        }
        if (address.isEmpty()) {
            throw new AddressException("No hay direcciones de mail validas en " + StringUtils.join(s, ";"));
        }
        return address.toArray(new InternetAddress[0]);
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    protected void sendMsg(final Session session, MailConfig config, final MimeMessage msg) throws MessagingException {
        if (config.isAuth()) {
            final String passw = config.getPassword();
            final Transport t = session.getTransport("smtp");
            t.connect(config.getUser(), passw);
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();
        } else {
            Transport.send(msg);
        }
    }

    protected Session getNewSession(final String user, final Boolean auth) {
        // create some properties and get the default Session
        //final Properties props = System.getProperties();
        final Properties props = new Properties(System.getProperties());
        props.setProperty("mail.smtp.host", getConfig().getHost());
        props.setProperty("mail.smtp.starttls.enable", getConfig().getTlsEnabled());
        props.setProperty("mail.smtp.port", getConfig().getPort());
        props.setProperty("mail.smtp.user", user);
        props.setProperty("mail.smtp.auth", auth.toString());
        if (StringUtils.isNotEmpty(getConfig().getTlsVersion())) {
            props.put("mail.smtp.ssl.protocols", getConfig().getTlsVersion());
        }
        if (getConfig().isSsl()) {
            props.put("mail.smtp.socketFactory.port", getConfig().getPort());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        final Session session = Session.getInstance(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(getConfig().getUser(), getConfig().getPassword());
            }
        });
        session.setDebug(getConfig().isDebug());
        return session;
    }

    public MailConfig getConfig() {
        return config;
    }

    public void setConfig(MailConfig config) {
        this.config = config;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPkPassword() {
        return pkPassword;
    }

    public void setPkPassword(String pkPassword) {
        this.pkPassword = pkPassword;
    }

    protected MimeMessage initMessage(final Session session, String replyTo, String[] to, String subject, String body) throws MessagingException {
        final MimeMessage msg = initBasicMessage(session, replyTo, to, subject);
        msg.setContent(body, "text/html");
        return msg;
    }

    protected void prependHeader(MimeMessage message, String signatureHeader)
            throws MessagingException {
        final List<String> prevHeader = new LinkedList<>();
        // read all the headers
        for (Enumeration<String> e = message.getAllHeaderLines(); e.hasMoreElements();) {
            final String headerLine = e.nextElement();
            prevHeader.add(headerLine);
        }
        // remove all the headers
        for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
            final Header header = e.nextElement();
            message.removeHeader(header.getName());
        }
        // add our header
        message.addHeaderLine(signatureHeader);
        // add the remaining headers using "addHeaderLine" that won't alter the
        // insertion order.
        for (String header : prevHeader) {
            message.addHeaderLine(header);
        }
    }

    protected MimeMessage initBasicMessage(final Session session, String replyTo, String[] to, String subject) throws MessagingException {
        // create a message
        final MimeMessage msg = new MimeMessage(session);
        if (replyTo != null) {
            final Address[] replyTos = {new InternetAddress(replyTo)};
            msg.setReplyTo(replyTos);
        } else if (getConfig().getReplyTo() != null) {
            final Address[] replyTos = {new InternetAddress(getConfig().getReplyTo())};
            msg.setReplyTo(replyTos);
        }
        try {
            msg.setFrom(new InternetAddress(getConfig().getFrom(), getConfig().getFromName()));
        } catch (UnsupportedEncodingException ex) {
            msg.setFrom(new InternetAddress(getConfig().getFrom()));
        }
        msg.setRecipients(Message.RecipientType.TO, getAddress(to));
        if (getAppname() != null) {
            msg.setSubject("[" + getAppname() + "] " + subject, "UTF-8");
        } else {
            msg.setSubject(subject, "UTF-8");
        }
        msg.setSentDate(new Date());
        return msg;
    }

    public static String getNickname(final String mail) {
        final String[] s = mail.split("[@]");
        return s[0];
    }

    private static class AsyncMailSender extends Thread {

        private final MailManager mgr;
        private final Mail mail;

        public AsyncMailSender(MailManager mgr, Mail mail) {
            this.mgr = mgr;
            this.mail = mail;
        }

        @Override
        public void run() {
            try {
                JPMUtils.getLogger().debug("Enviando mail a " + StringUtils.join(mail.getTo(), ";"));
                mgr.send(mail);
            } catch (MessagingException ex) {
                JPMUtils.getLogger().error("Error al enviar mail a " + StringUtils.join(mail.getTo(), ";"), ex);
            }
        }

    }
}
