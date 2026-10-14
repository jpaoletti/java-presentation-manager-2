package jpaoletti.jpm2.core.model.persistent;

/**
 * Kind of item of the dynamic menu ({@link JpmMenu}).
 *
 * @author jpaoletti
 */
public enum JpmMenuType {

    /** Container of other items (dropdown on the top level, submenu below it). */
    GROUP,
    /** Link to an entity operation ({@code jpm/<entity>/<operation>}). */
    ENTITY,
    /** Link to a free URL, relative to the context path or absolute. */
    URL,
    /** Non clickable title inside a group. */
    HEADER,
    /** Divider line inside a group. */
    SEPARATOR;

    public boolean isLink() {
        return this == ENTITY || this == URL;
    }
}
