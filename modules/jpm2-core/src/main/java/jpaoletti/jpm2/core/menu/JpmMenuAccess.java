package jpaoletti.jpm2.core.menu;

import java.util.List;

/**
 * What {@link JpmMenuTree} needs to know about the current user and the
 * application to decide which menu items are shown.
 *
 * @author jpaoletti
 */
public interface JpmMenuAccess {

    /**
     * @param authorities non empty list of authorities
     * @return true when the user has any of them
     */
    boolean hasAnyAuthority(List<String> authorities);

    /**
     * @param entity entity id, may include a context ({@code entity!context})
     * @param operation operation id
     * @return true when the user can access the entity and the operation
     */
    boolean canAccess(String entity, String operation);

    /**
     * @param item a menu item
     * @return the label ready to print (escaped when it is literal text)
     */
    String labelHtml(JpmMenuItem item);

    /**
     * Resolves the {@code {user.<property>}} placeholders of a URL item.
     *
     * @param url the configured url
     * @return the url to render, or null to hide the item (a placeholder that
     * can't be resolved for the current user)
     */
    default String resolveUrl(String url) {
        return url;
    }

    /**
     * @param condition {@code user.<property>} or {@code !user.<property>}
     * @return true when the current user meets the condition
     */
    default boolean matchesUserCondition(String condition) {
        return true;
    }
}
