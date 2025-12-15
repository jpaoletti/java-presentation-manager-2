package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.security.User;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Filter that restricts the list of available groups based on the current user's privilege level.
 * Only shows groups with level >= current user's level (equal or lower privilege).
 *
 * Hierarchy logic: 1 = maximum privilege, higher numbers = lower privilege.
 * A user with level 2 can only see/assign groups with level >= 2 (2, 3, 4, ...)
 *
 * @author jpaoletti
 */
public class GroupLevelCollectionFilter implements ListFilter {

    private static final String FILTER_ID = "groupLevelCollectionFilter";

    @Override
    public String getId() {
        return FILTER_ID;
    }

    @Override
    public Criterion getListFilter(
            DAOListConfiguration dlc,
            Entity entity,
            SessionEntityData sessionData,
            String currentId,
            String owner,
            String ownerId) {

        try {
            // Get current authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof User)) {
                // If not authenticated or not a User, show all groups (system operation)
                return null;
            }

            User currentUser = (User) principal;
            Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();

            // Only show groups with level >= current user's level
            // (equal or lower privilege than current user)
            return Restrictions.ge("level", currentUserLevel);

        } catch (Exception e) {
            // If any error occurs, show all groups (fail-open for system operations)
            return null;
        }
    }
}
