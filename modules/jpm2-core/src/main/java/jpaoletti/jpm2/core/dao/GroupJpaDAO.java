package jpaoletti.jpm2.core.dao;

import java.util.List;
import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.security.PrivilegeLevelGuard;
import jpaoletti.jpm2.core.security.User;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class GroupJpaDAO extends JPADAO<Group, Long> implements InstanceAccessGuard {

    @Override
    public Long getId(Object object) {
        return ((Group) object).getId();
    }

    @Override
    public boolean canAccess(Object instance) {
        return PrivilegeLevelGuard.canAccess(instance);
    }

    @Override
    public Group find(IDAOListConfiguration configuration) {
        return super.find(applyCurrentUserFilter(configuration));
    }

    @Override
    public Long count(IDAOListConfiguration configuration) {
        return super.count(applyCurrentUserFilter(configuration));
    }

    @Override
    public List<Group> list(IDAOListConfiguration configuration) {
        return super.list(applyCurrentUserFilter(configuration));
    }

    protected JPADAOListConfiguration applyCurrentUserFilter(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = configuration instanceof JPADAOListConfiguration
                ? ((JPADAOListConfiguration) configuration).clone()
                : build();

        final User currentUser = PrivilegeLevelGuard.getCurrentUser();
        // Not authenticated or not a User: system operation, no filter
        if (currentUser != null) {
            try {
                final Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();
                cfg.withPredicate((cb, root) -> cb.ge(root.get("level"), currentUserLevel));
            } catch (Exception e) {
                // The level of an authenticated user could not be computed: show nothing
                JPMUtils.getLogger().warn("Error applying group level filter", e);
                cfg.withPredicate((cb, root) -> cb.disjunction());
            }
        }

        return cfg;
    }
}
