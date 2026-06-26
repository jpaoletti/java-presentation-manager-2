package jpaoletti.jpm2.core.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jpaoletti.jpm2.core.model.persistent.Notification;

public class NotificationDAO extends DefaultJPADAO {

    @Override
    public Object get(String id) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Notification> query = cb.createQuery(Notification.class);
        Root<Notification> root = query.from(Notification.class);
        query.select(root)
                .where(
                        cb.and(
                                cb.equal(root.get("id"), Long.parseLong(id)),
                                cb.equal(root.get("username"), getAuthorizationService().getCurrentUsername())
                        )
                );
        List<Notification> result = entityManager.createQuery(query).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public JPADAOListConfiguration build() {
        JPADAOListConfiguration cfg = super.build();
        addRestrictions(cfg);
        return cfg;
    }

    public void addRestrictions(JPADAOListConfiguration cfg) {
        cfg.withPredicate((cb, root) -> cb.equal(root.get("username"), getAuthorizationService().getCurrentUsername()));
    }
}
