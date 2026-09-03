package jpaoletti.jpm2.core.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    private final List<InlineAttachment> inlineAttachments = new ArrayList<>();

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

    public List<InlineAttachment> getInlineAttachments() {
        return Collections.unmodifiableList(inlineAttachments);
    }

    public void addInlineAttachment(String contentId, String filename, String contentType, byte[] content) {
        addInlineAttachment(new InlineAttachment(contentId, filename, contentType, content));
    }

    public void addInlineAttachment(InlineAttachment attachment) {
        Objects.requireNonNull(attachment, "attachment");
        if (inlineAttachments.stream().anyMatch(existing
                -> existing.getContentId().equalsIgnoreCase(attachment.getContentId()))) {
            throw new IllegalArgumentException("Duplicated inline attachment contentId: "
                    + attachment.getContentId());
        }
        inlineAttachments.add(attachment);
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}
