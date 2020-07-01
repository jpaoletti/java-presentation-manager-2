package jpaoletti.jpm2.core.model;

/**
 *
 * @author jpaoletti
 */
public interface WithAttachment {

    public byte[] getAttachment();

    public String getAttachmentName();

    public String getContentType();
}
