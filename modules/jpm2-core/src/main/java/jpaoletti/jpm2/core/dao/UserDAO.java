package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.JpmUser;
import jpaoletti.jpm2.core.security.User;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jpaoletti
 */
public class UserDAO extends HibernateCriteriaDAO<JpmUser, String> {

    @Override
    public String getId(Object object) {
        return ((JpmUser) object).getUsername();
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

        try {
            // Get current authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                User currentUser = (User) principal;
                Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();

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
            }
            // If not authenticated or not a User, show all users (system operation)
        } catch (Exception e) {
            // If any error occurs, show all users (fail-open for system operations)
            JPMUtils.getLogger().warn("Error applying user level filter", e);
        }

        return criteria;
    }
}
