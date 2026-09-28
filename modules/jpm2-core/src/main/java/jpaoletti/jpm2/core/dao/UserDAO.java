package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.JpmUser;
import jpaoletti.jpm2.core.security.PrivilegeLevelGuard;
import jpaoletti.jpm2.core.security.User;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class UserDAO extends HibernateCriteriaDAO<JpmUser, String> implements InstanceAccessGuard {

    @Override
    public String getId(Object object) {
        return ((JpmUser) object).getUsername();
    }

    @Override
    public boolean canAccess(Object instance) {
        return PrivilegeLevelGuard.canAccess(instance);
    }

    /**
     * Override getBaseCriteria to filter users based on current user's
     * privilege level. Users can only see other users whose maximum privilege
     * level >= their own level (equal or lower privilege).
     *
     * This uses a SQL restriction with a subquery to calculate each user's
     * minimum group level (maximum privilege) and filters accordingly.
     *
     * @param configuration
     * @return
     */
    @Override
    public Criteria getBaseCriteria(IDAOListConfiguration configuration) {
        Criteria criteria = super.getBaseCriteria(configuration);

        final User currentUser = PrivilegeLevelGuard.getCurrentUser();
        // If not authenticated or not a User, show all users (system operation)
        if (currentUser != null) {
            try {
                final Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();
                // Use SQL restriction to filter users whose MIN(group.level) >= currentUserLevel
                // This subquery calculates the minimum level across all groups for each user
                criteria.add(Restrictions.sqlRestriction(
                        "exists ("
                        + "  select 1 from group_members gm "
                        + "  inner join jpm_groups g on g.id = gm.group_id "
                        + "  where gm.username = {alias}.username "
                        + "  group by gm.username "
                        + "  having min(g.hierarchy_level) >= ?"
                        + ")",
                        currentUserLevel,
                        org.hibernate.type.StandardBasicTypes.INTEGER
                ));
            } catch (Exception e) {
                // The level of an authenticated user could not be computed: show nothing
                JPMUtils.getLogger().warn("Error applying user level filter", e);
                criteria.add(Restrictions.sqlRestriction("1=0"));
            }
        }

        return criteria;
    }
}
