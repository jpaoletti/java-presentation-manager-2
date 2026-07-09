package jpaoletti.jpm2.core.mail;

import java.io.File;

/**
 * Value object describing a single email to be sent.
 *
 * @author jpaoletti
 */
public class Mail {

    private String subject;
    private String body;
    private String textbody;
    private String dkimSignTemplate;
    private String replyTo;
    private String[] to;
    private String[] cc;
    private String[] cco;
    private File[] attachs;

    public Mail(String subject, String body, String... to) {
        this.subject = subject;
        this.body = body;
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTextbody() {
        return textbody;
    }

    public void setTextbody(String textbody) {
        this.textbody = textbody;
    }

    public String getDkimSignTemplate() {
        return dkimSignTemplate;
    }

    public void setDkimSignTemplate(String dkimSignTemplate) {
        this.dkimSignTemplate = dkimSignTemplate;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String... to) {
        this.to = to;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String... cc) {
        this.cc = cc;
    }

    public String[] getCco() {
        return cco;
    }

    public void setCco(String... cco) {
        this.cco = cco;
    }

    public File[] getAttachs() {
        return attachs;
    }

    public void setAttachs(File... attachs) {
        this.attachs = attachs;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}
