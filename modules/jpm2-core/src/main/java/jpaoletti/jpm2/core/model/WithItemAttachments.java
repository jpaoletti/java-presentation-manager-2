package jpaoletti.jpm2.core.model;

/**
 * Contract for an entity that owns weak {@link ItemAttachment} notes/attachments.
 *
 * @author jpaoletti
 */
public interface WithItemAttachments {

    public Long getId();

    public ItemAttachment newAttachment();
}
