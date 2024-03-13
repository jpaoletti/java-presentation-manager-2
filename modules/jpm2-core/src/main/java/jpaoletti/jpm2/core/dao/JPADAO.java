package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
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
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(HibernateCriteriaDAO.class, getClass());
        this.persistentClass = (Class<T>) typeArguments[0];
        this.idClass = (Class<ID>) typeArguments[1];
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

    public JPADAOListConfiguration build() {
        final EntityManager em = getEntityManager();
        final JPADAOListConfiguration res = new JPADAOListConfiguration(em);
        return res;
    }

    @Override
    public Long count(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = (JPADAOListConfiguration) configuration;
        final CriteriaBuilder cb = initCriteriaBuilder(cfg);

        final CriteriaQuery<Long> cq = cfg.getCriteriaBuilder().createQuery(Long.class);

        cq.select(cfg.getCriteriaBuilder().count(cq.from(getPersistentClass())));
        final ParameterExpression<Integer> p = cb.parameter(Integer.class);
        cq.where(cb.equal(cq.from(getPersistentClass()).get("age"), p));

        return cfg.getEntityManager().createQuery(cq).getSingleResult();

    }

    @Override
    public List<T> list(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = (JPADAOListConfiguration) configuration;
        final TypedQuery query = getBaseCriteria(configuration);
        if (configuration != null) {
            if (configuration.getMax() != null) {
                query.setMaxResults(configuration.getMax());
            }
            if (configuration.getFrom() != null) {
                query.setFirstResult(configuration.getFrom());
            }
        }
        /*final Root root = query.from(getPersistentClass());
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();
        if (!cfg.getOrders().isEmpty()) {
            for (DAOOrder order : cfg.getOrders()) {
                if (order.isAsc()) {
                    cq.orderBy(cb.asc(root.get(order.getOrder())));
                } else {
                    cq.orderBy(cb.desc(root.get(order.getOrder())));
                }
            }
        }*/
 /*
            if (!cfg.getProperties().isEmpty()) {
                final ProjectionList projectionList = Projections.projectionList();
                for (Map.Entry<String, String> entry : configuration.getProperties().entrySet()) {
                    projectionList.add(Projections.property(entry.getKey()), entry.getValue());
                }
                c.setProjection(projectionList);
            }*/
        return query.getResultList();
    }

    public CriteriaBuilder initCriteriaBuilder(JPADAOListConfiguration cfg) {
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();
        final ParameterExpression<Integer> p = cb.parameter(Integer.class);
        //cq.where(cb.equal(cq.from(getPersistentClass()).get("age"), p));
        return cb;
    }

    public TypedQuery getBaseCriteria(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = (JPADAOListConfiguration) configuration;
        final CriteriaQuery<T> cq = cfg.getCriteriaBuilder().createQuery(getPersistentClass());
        cq.from(getPersistentClass());
        final TypedQuery query = cfg.getEntityManager().createQuery(cq);
        //c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        final Root root = cq.from(getPersistentClass());
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();

        if (configuration != null) {
            /*for (DAOListConfiguration.DAOListConfigurationAlias alias : cfg.getAliases()) {
                    if (alias.getJoinType() != null) {
                        cq = cq.createAlias(alias.getProperty(), alias.getAlias(), alias.getJoinType());
                    } else {
                        cq = cq.createAlias(alias.getProperty(), alias.getAlias());
                    }
                }*/
 /*for (Criterion criterion : cfg.getRestrictions()) {
                if (criterion != null) {
                    cq.add(criterion);
                }
            }
            
        ParameterExpression<String> paramTitle = cb.parameter(String.class);
        cq.where(cb.like(books.get(Book_.title), paramTitle));
        query.setParameter(paramTitle, "%Hibernate%");

            
            
             */
        }
        return query;
    }

    public CriteriaQuery getBaseCriteria2(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = (JPADAOListConfiguration) configuration;
        final CriteriaQuery<T> cq = cfg.getCriteriaBuilder().createQuery(getPersistentClass());
        cq.from(getPersistentClass());
        final Root root = cq.from(getPersistentClass());
        final CriteriaBuilder cb = cfg.getCriteriaBuilder();

        if (configuration != null) {
            /*for (DAOListConfiguration.DAOListConfigurationAlias alias : cfg.getAliases()) {
                    if (alias.getJoinType() != null) {
                        cq = cq.createAlias(alias.getProperty(), alias.getAlias(), alias.getJoinType());
                    } else {
                        cq = cq.createAlias(alias.getProperty(), alias.getAlias());
                    }
                }*/
 /*for (Criterion criterion : cfg.getRestrictions()) {
                if (criterion != null) {
                    cq.add(criterion);
                }
            }
            
        ParameterExpression<String> paramTitle = cb.parameter(String.class);
        cq.where(cb.like(books.get(Book_.title), paramTitle));
        query.setParameter(paramTitle, "%Hibernate%");

            
            
             */
        }
        return cq;
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

}
