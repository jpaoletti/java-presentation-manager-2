package jpaoletti.jpm2.core.security;

import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Privilege level rules for users and groups. A lower level is a higher
 * privilege: the current user can manage groups whose level is equal or
 * greater than its own, and users that it can manage (see
 * {@link User#canManage(User)}).
 *
 * When there is no authenticated User (system threads, login) everything is
 * accessible. When the level can't be computed, nothing is.
 *
 * @author jpaoletti
 */
public final class PrivilegeLevelGuard {

    private PrivilegeLevelGuard() {
    }

    /**
     * @return the current authenticated User or null
     */
    public static User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public static boolean canAccess(Object instance) {
        final User current = getCurrentUser();
        if (current == null || instance == null) {
            return true;
        }
        try {
            if (instance instanceof Group) {
                final Integer level = ((Group) instance).getLevel();
                return level == null || level >= current.getMaxPrivilegeLevel();
            }
            if (instance instanceof User) {
                return current.getUsername().equals(((User) instance).getUsername()) || current.canManage((User) instance);
            }
            return true;
        } catch (Exception e) {
            JPMUtils.getLogger().warn("Error checking privilege level, access denied", e);
            return false;
        }
    }
}
