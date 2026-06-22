package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.idtransformer.IdTransformer;
import jpaoletti.jpm2.core.service.AuthorizationService;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.jodah.typetools.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author jpaoletti
 * @param <T>
 * @param <ID>
 */
public abstract class HibernateCriteriaDAO<T, ID extends Serializable> implements DAO<T, ID> {

    private static final org.apache.logging.log4j.Logger LOG = JPMUtils.getLogger(JPMUtils.DAO);

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private AuthorizationService authorizationService;

    private Class<T> persistentClass;
    protected Class<ID> idClass;
    private IdTransformer<ID> transformer;

    public HibernateCriteriaDAO() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(HibernateCriteriaDAO.class, getClass());
        this.persistentClass = (Class<T>) typeArguments[0];
        this.idClass = (Class<ID>) typeArguments[1];
    }

    @Override
    public T get(String id) {
        LOG.debug("get IN class={} id={}", getPersistentClass().getSimpleName(), id);
        if (id == null) {
            return null;
        }
        if (id.getClass().equals(idClass)) {
            final T result = (T) getSession().get(getPersistentClass(), id);
            LOG.debug("get OUT class={} id={} found={}", getPersistentClass().getSimpleName(), id, result != null);
            return result;
        } else {
            try {
                final T result = (T) getSession().get(getPersistentClass(), (Serializable) getTransformer().transform(id));
                LOG.debug("get OUT class={} id={} (transformed) found={}", getPersistentClass().getSimpleName(), id, result != null);
                return result;
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
        LOG.debug("update class={}", getPersistentClass().getSimpleName());
        getSession().update(object);
    }

    @Override
    public void save(Object object) {
        LOG.debug("save class={}", getPersistentClass().getSimpleName());
        getSession().save(object);
    }

    @Override
    public void delete(Object object) {
        LOG.debug("delete class={}", getPersistentClass().getSimpleName());
        getSession().delete(object);
    }

    /**
     * Deletes all.
     */
    public void deleteAll() {
        getSession().createQuery("DELETE FROM " + getPersistentClass().getName()).executeUpdate();
    }

    @Override
    public DAOListConfiguration build() {
        return new DAOListConfiguration();
    }

    @Override
    public Long count(IDAOListConfiguration configuration) {
        final Criteria c = getBaseCriteria(configuration);
        c.setFirstResult(0);
        c.setProjection(Projections.rowCount());
        final Long total = (Long) c.uniqueResult();
        LOG.debug("count class={} total={}", getPersistentClass().getSimpleName(), total);
        return total;
    }

    @Override
    public List<T> list(IDAOListConfiguration configuration) {
        final DAOListConfiguration cfg = (DAOListConfiguration) configuration;
        final Criteria c = getBaseCriteria(configuration);
        if (configuration != null) {
            if (!cfg.getProperties().isEmpty()) {
                final ProjectionList projectionList = Projections.projectionList();
                for (Map.Entry<String, String> entry : configuration.getProperties().entrySet()) {
                    projectionList.add(Projections.property(entry.getKey()), entry.getValue());
                }
                c.setProjection(projectionList);
            }
        }
        final List<T> result = c.list();
        if (LOG.isDebugEnabled()) {
            LOG.debug("list class={} from={} max={} size={}", getPersistentClass().getSimpleName(),
                    configuration != null ? configuration.getFrom() : null,
                    configuration != null ? configuration.getMax() : null, result.size());
        }
        return result;
    }

    public Criteria getBaseCriteria(IDAOListConfiguration configuration) {
        final DAOListConfiguration cfg = (DAOListConfiguration) configuration;
        Criteria c = getSession().createCriteria(getPersistentClass());
        c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        if (configuration != null) {
            if (configuration.getMax() != null) {
                c.setMaxResults(configuration.getMax());
            }
            if (configuration.getFrom() != null) {
                c.setFirstResult(configuration.getFrom());
            }
            for (DAOListConfiguration.DAOListConfigurationAlias alias : cfg.getAliases()) {
                if (alias.getJoinType() != null) {
                    c = c.createAlias(alias.getProperty(), alias.getAlias(), alias.getJoinType());
                } else {
                    c = c.createAlias(alias.getProperty(), alias.getAlias());
                }
            }
            if (!cfg.getOrders().isEmpty()) {
                for (Order order : cfg.getOrders()) {
                    c.addOrder(order);
                }
            }
            for (Criterion criterion : cfg.getRestrictions()) {
                if (criterion != null) {
                    c.add(criterion);
                }
            }
        }
        return c;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public IdTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(IdTransformer transformer) {
        this.transformer = transformer;
    }

    /**
     * @return the persistentClass
     */
    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    /**
     * @param persistentClass the persistentClass to set
     */
    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    @Override
    public void detach(Object object) {
        getSession().evict(object);
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
