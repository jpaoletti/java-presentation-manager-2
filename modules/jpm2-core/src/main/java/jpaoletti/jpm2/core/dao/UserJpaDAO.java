package jpaoletti.jpm2.core.dao;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.security.JpmUser;
import jpaoletti.jpm2.core.security.User;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jpaoletti
 */
public class UserJpaDAO extends JPADAO<JpmUser, String> {

    @Override
    public String getId(Object object) {
        return ((JpmUser) object).getUsername();
    }

    @Override
    public Long count(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = configuration instanceof JPADAOListConfiguration
                ? (JPADAOListConfiguration) configuration
                : build();
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<JpmUser> root = cq.from(getPersistentClass());

        Predicate basePredicate = buildConfigurationPredicate(cfg, cb, root);
        Predicate securityPredicate = buildCurrentUserPredicate(cb, cq, root);

        if (basePredicate != null && securityPredicate != null) {
            cq.where(cb.and(basePredicate, securityPredicate));
        } else if (basePredicate != null) {
            cq.where(basePredicate);
        } else if (securityPredicate != null) {
            cq.where(securityPredicate);
        }

        cq.select(cb.countDistinct(root));
        return cfg.getEntityManager().createQuery(cq).getSingleResult();
    }

    @Override
    protected TypedQuery<JpmUser> buildQuery(JPADAOListConfiguration cfg) {
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();
        final CriteriaQuery<JpmUser> cq = cb.createQuery(getPersistentClass());
        final Root<JpmUser> root = cq.from(getPersistentClass());

        Predicate basePredicate = buildConfigurationPredicate(cfg, cb, root);
        Predicate securityPredicate = buildCurrentUserPredicate(cb, cq, root);

        if (basePredicate != null && securityPredicate != null) {
            cq.where(cb.and(basePredicate, securityPredicate));
        } else if (basePredicate != null) {
            cq.where(basePredicate);
        } else if (securityPredicate != null) {
            cq.where(securityPredicate);
        }

        applyOrdersAndProjection(cfg, cb, cq, root);
        return cfg.getEntityManager().createQuery(cq);
    }

    protected Predicate buildConfigurationPredicate(JPADAOListConfiguration cfg, CriteriaBuilder cb, Root<JpmUser> root) {
        for (JPADAOListConfiguration.JPAAlias alias : cfg.getAliases()) {
            root.join(alias.getProperty(), alias.getJoinType()).alias(alias.getAlias());
        }

        if (cfg.getPredicates().isEmpty()) {
            return null;
        }

        return cb.and(cfg.getPredicates().stream()
                .map(predicate -> predicate.apply(cb, root))
                .filter(predicate -> predicate != null)
                .toArray(Predicate[]::new));
    }

    protected void applyOrdersAndProjection(JPADAOListConfiguration cfg, CriteriaBuilder cb, CriteriaQuery<JpmUser> cq, Root<JpmUser> root) {
        if (!cfg.getOrders().isEmpty()) {
            cq.orderBy(cfg.getOrders().stream()
                    .map(order -> order.isAsc() ? cb.asc(root.get(order.getOrder())) : cb.desc(root.get(order.getOrder())))
                    .toList());
        }

        if (!cfg.getProperties().isEmpty()) {
            java.util.List<Selection<?>> selections = new java.util.ArrayList<>();
            cfg.getProperties().forEach((key, value) -> selections.add(root.get(key).alias(value)));
            cq.multiselect(selections);
        } else {
            cq.select(root).distinct(true);
        }
    }

    protected Predicate buildCurrentUserPredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<JpmUser> root) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                User currentUser = (User) principal;
                Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();
                Subquery<String> subquery = cq.subquery(String.class);
                Root<JpmUser> subRoot = subquery.from(JpmUser.class);
                Join<JpmUser, Group> groups = subRoot.join("groups");
                subquery.select(subRoot.get("username"));
                subquery.where(cb.equal(subRoot.get("username"), root.get("username")));
                subquery.groupBy(subRoot.get("username"));
                subquery.having(cb.ge(cb.min(groups.get("level").as(Integer.class)), currentUserLevel));
                return cb.exists(subquery);
            }
        } catch (Exception e) {
            JPMUtils.getLogger().warn("Error applying user level filter", e);
        }
        return null;
    }
}
