package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.security.PrivilegeLevelGuard;
import jpaoletti.jpm2.core.security.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class GroupDAO extends HibernateCriteriaDAO<Group, Long> implements InstanceAccessGuard {

    @Override
    public Long getId(Object object) {
        return ((Group) object).getId();
    }

    @Override
    public boolean canAccess(Object instance) {
        return PrivilegeLevelGuard.canAccess(instance);
    }

    /**
     * Override getBaseCriteria to filter groups based on current user's privilege level.
     * Users can only see groups with level >= their own level (equal or lower privilege).
     */
    @Override
    public Criteria getBaseCriteria(IDAOListConfiguration configuration) {
        Criteria criteria = super.getBaseCriteria(configuration);

        final User currentUser = PrivilegeLevelGuard.getCurrentUser();
        // If not authenticated or not a User, show all groups (system operation)
        if (currentUser != null) {
            try {
                // Only show groups with level >= current user's level
                // (equal or lower privilege than current user)
                criteria.add(Restrictions.ge("level", currentUser.getMaxPrivilegeLevel()));
            } catch (Exception e) {
                // The level of an authenticated user could not be computed: show nothing
                JPMUtils.getLogger().warn("Error applying group level filter", e);
                criteria.add(Restrictions.sqlRestriction("1=0"));
            }
        }

        return criteria;
    }
}
