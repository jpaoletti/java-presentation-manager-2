package jpaoletti.jpm2.core.model;

import java.util.Date;

/**
 * Contract for a weak attachment ("note") that belongs to a
 * {@link WithItemAttachments} owner. Extends {@link WithAttachment} with note
 * metadata (author, timestamp, position and the binary payload).
 *
 * @author jpaoletti
 */
public interface ItemAttachment extends WithAttachment {

    public Long getId();

    public String getNote();

    public Date getNoteDatetime();

    public Integer getPos();

    public String getUser();

    public void setAttachment(byte[] attachment);

    public void setAttachmentOriginalName(String attachmentOriginalName);

    public void setContentType(String contentType);

    public void setNote(String note);

    public void setNoteDatetime(Date noteDatetime);

    public void setPos(Integer pos);

    public void setUser(String user);

    public WithItemAttachments getOwner();

}
