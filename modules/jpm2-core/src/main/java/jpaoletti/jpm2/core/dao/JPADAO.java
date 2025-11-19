package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jpaoletti.jpm2.core.idtransformer.IdTransformer;
import jpaoletti.jpm2.core.service.AuthorizationService;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jodah.typetools.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * https://thorben-janssen.com/migration-criteria-api/
 *
 * @author jpaoletti
 * @param <T>
 * @param <ID>
 */
public abstract class JPADAO<T, ID extends Serializable> implements DAO<T, ID> {

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private AuthorizationService authorizationService;

    private Class<T> persistentClass;
    protected Class<ID> idClass;
    private IdTransformer<ID> transformer;

    public JPADAO() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(JPADAO.class, getClass());
        if (typeArguments != null && typeArguments.length >= 2) {
            this.persistentClass = (Class<T>) typeArguments[0];
            this.idClass = (Class<ID>) typeArguments[1];
        }
    }

    @Override
    public T get(String id) {
        if (id == null) {
            return null;
        }
        if (id.getClass().equals(idClass)) {
            return (T) getSession().get(getPersistentClass(), id);
        } else {
            try {
                return (T) getSession().get(getPersistentClass(), (Serializable) getTransformer().transform(id));
            } catch (Exception e) {
                JPMUtils.getLogger().warn("Invalid ID transformation", e);
                return null;
            }
        }
    }

    @Override
    public T find(IDAOListConfiguration configuration) {
        Integer max = configuration.getMax();
        configuration.setMax(1);
        final List<T> list = list(configuration);
        configuration.setMax(max);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void update(final Object object) {
        getSession().update(object);
    }

    @Override
    public void save(Object object) {
        getSession().save(object);
    }

    @Override
    public void delete(Object object) {
        getSession().delete(object);
    }

    /**
     * Deletes all.
     */
    public void deleteAll() {
        getSession().createQuery("DELETE FROM " + getPersistentClass().getName()).executeUpdate();
    }

    @Override
    public JPADAOListConfiguration build() {
        final EntityManager em = getEntityManager();
        final JPADAOListConfiguration res = new JPADAOListConfiguration(em);
        return res;
    }

    @Override
    public Long count(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = (JPADAOListConfiguration) configuration;
        final EntityManager em = cfg.getEntityManager();
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<T> root = cq.from(getPersistentClass());

        // Apply aliases/joins
        for (JPADAOListConfiguration.JPAAlias alias : cfg.getAliases()) {
            root.join(alias.getProperty(), alias.getJoinType()).alias(alias.getAlias());
        }

        // Apply predicates
        if (!cfg.getPredicates().isEmpty()) {
            List<Predicate> predicateList = new ArrayList<>();
            for (java.util.function.BiFunction<CriteriaBuilder, javax.persistence.criteria.Root, Predicate> predicateFunc : cfg.getPredicates()) {
                Predicate p = predicateFunc.apply(cb, root);
                if (p != null) {
                    predicateList.add(p);
                }
            }
            if (!predicateList.isEmpty()) {
                cq.where(cb.and(predicateList.toArray(new Predicate[0])));
            }
        }

        cq.select(cb.count(root));

        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public List<T> list(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = (JPADAOListConfiguration) configuration;
        final TypedQuery<T> query = buildQuery(cfg);

        if (configuration != null) {
            if (configuration.getMax() != null) {
                query.setMaxResults(configuration.getMax());
            }
            if (configuration.getFrom() != null) {
                query.setFirstResult(configuration.getFrom());
            }
        }

        return query.getResultList();
    }

    /**
     * Builds a typed query from the configuration applying all predicates, aliases, orders, and projections
     *
     * @param cfg JPA DAO list configuration
     * @return TypedQuery ready to execute
     */
    protected TypedQuery<T> buildQuery(JPADAOListConfiguration cfg) {
        final EntityManager em = cfg.getEntityManager();
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();
        final CriteriaQuery<T> cq = cb.createQuery(getPersistentClass());
        final Root<T> root = cq.from(getPersistentClass());

        // Apply aliases/joins
        for (JPADAOListConfiguration.JPAAlias alias : cfg.getAliases()) {
            root.join(alias.getProperty(), alias.getJoinType()).alias(alias.getAlias());
        }

        // Apply predicates (restrictions)
        if (!cfg.getPredicates().isEmpty()) {
            List<Predicate> predicateList = new ArrayList<>();
            for (java.util.function.BiFunction<CriteriaBuilder, javax.persistence.criteria.Root, Predicate> predicateFunc : cfg.getPredicates()) {
                Predicate p = predicateFunc.apply(cb, root);
                if (p != null) {
                    predicateList.add(p);
                }
            }
            if (!predicateList.isEmpty()) {
                cq.where(cb.and(predicateList.toArray(new Predicate[0])));
            }
        }

        // Apply orders
        if (!cfg.getOrders().isEmpty()) {
            List<javax.persistence.criteria.Order> orders = new ArrayList<>();
            for (DAOOrder order : cfg.getOrders()) {
                if (order.isAsc()) {
                    orders.add(cb.asc(root.get(order.getOrder())));
                } else {
                    orders.add(cb.desc(root.get(order.getOrder())));
                }
            }
            cq.orderBy(orders);
        }

        // Apply projections if specified
        if (!cfg.getProperties().isEmpty()) {
            List<javax.persistence.criteria.Selection<?>> selections = new ArrayList<>();
            for (Map.Entry<String, String> entry : cfg.getProperties().entrySet()) {
                selections.add(root.get(entry.getKey()).alias(entry.getValue()));
            }
            cq.multiselect(selections);
        } else {
            // Select distinct root entity to avoid duplicates from joins
            cq.select(root).distinct(true);
        }

        return em.createQuery(cq);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Class<ID> getIdClass() {
        return idClass;
    }

    public void setIdClass(Class<ID> idClass) {
        this.idClass = idClass;
    }

    public IdTransformer<ID> getTransformer() {
        return transformer;
    }

    public void setTransformer(IdTransformer<ID> transformer) {
        this.transformer = transformer;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public EntityManager getEntityManager() {
        return getSession().getEntityManagerFactory().createEntityManager();
    }

    @Override
    public void detach(Object object) {
        getSession().evict(object);
    }

}
