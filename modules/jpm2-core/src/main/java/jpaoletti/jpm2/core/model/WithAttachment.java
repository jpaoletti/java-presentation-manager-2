package jpaoletti.jpm2.core.model;

/**
 *
 * @author jpaoletti
 */
public interface WithAttachment {

    public byte[] getAttachment();

    public String getAttachmentName();

    public String getContentType();

    public boolean isExternalFile();

    /**
     * In case the file is external, this must return the full path of the file.
     *
     * @return
     */
    public String getInternalFileName();

}
