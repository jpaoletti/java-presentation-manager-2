package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.security.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jpaoletti
 */
public class GroupDAO extends HibernateCriteriaDAO<Group, Long> {

    @Override
    public Long getId(Object object) {
        return ((Group) object).getId();
    }

    /**
     * Override getBaseCriteria to filter groups based on current user's privilege level.
     * Users can only see groups with level >= their own level (equal or lower privilege).
     */
    @Override
    public Criteria getBaseCriteria(IDAOListConfiguration configuration) {
        Criteria criteria = super.getBaseCriteria(configuration);

        try {
            // Get current authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                User currentUser = (User) principal;
                Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();

                // Only show groups with level >= current user's level
                // (equal or lower privilege than current user)
                criteria.add(Restrictions.ge("level", currentUserLevel));
            }
            // If not authenticated or not a User, show all groups (system operation)
        } catch (Exception e) {
            // If any error occurs, show all groups (fail-open for system operations)
        }

        return criteria;
    }
}
