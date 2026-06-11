package jpaoletti.jpm2.core.dao;

import java.util.List;
import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.security.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jpaoletti
 */
public class GroupJpaDAO extends JPADAO<Group, Long> {

    @Override
    public Long getId(Object object) {
        return ((Group) object).getId();
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

        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                User currentUser = (User) principal;
                Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();
                cfg.withPredicate((cb, root) -> cb.ge(root.get("level"), currentUserLevel));
            }
        } catch (Exception e) {
            // Show all groups on system operations or auth lookup failures.
        }

        return cfg;
    }
}
