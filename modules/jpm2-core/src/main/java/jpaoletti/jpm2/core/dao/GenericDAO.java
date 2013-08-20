package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.List;
import jpaoletti.jpm2.core.idtransformer.IdTransformer;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.jodah.typetools.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
public abstract class GenericDAO<T, ID extends Serializable> {

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;
    protected Class<T> persistentClass;
    protected Class<ID> idClass;
    private IdTransformer transformer;

    public GenericDAO() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(GenericDAO.class, getClass());
        this.persistentClass = (Class<T>) typeArguments[0];
        this.idClass = (Class<ID>) typeArguments[1];
    }

    public T get(ID id) {
        if (id.getClass().equals(idClass)) {
            return (T) getSession().get(persistentClass, id);
        } else {
            return (T) getSession().get(persistentClass, (Serializable) getTransformer().transform(id));
        }
    }

    @Transactional(readOnly = false)
    public void update(final Object object) {
        getSession().update(object);
    }

    @Transactional(readOnly = false)
    public void save(Object object) {
        getSession().save(object);
    }

    /**
     * Retorna una lista.
     */
    public List<T> list(Criterion... restrictions) {
        final Criteria c = getBaseCriteria(restrictions);
        return c.list();
    }

    /**
     * Retorna una lista paginada.
     */
    public List<T> list(Integer from, Integer max, Criterion... restrictions) {
        final Criteria c = getBaseCriteria(restrictions);
        c.setMaxResults(max);
        c.setFirstResult(from);
        return c.list();
    }

    /**
     * Retorna una lista con un solo ordenamiento.
     */
    public List<T> list(Order order, Criterion... restrictions) {
        final Criteria c = getBaseCriteria(restrictions);
        c.addOrder(order);
        return c.list();
    }

    /**
     * Retorna una lista con un solo ordenamiento.
     */
    public List<T> list(Integer from, Integer max, Order order, Criterion... restrictions) {
        final Criteria c = getBaseCriteria(restrictions);
        c.addOrder(order);
        c.setMaxResults(max);
        c.setFirstResult(from);
        return c.list();
    }

    protected Criteria getBaseCriteria(Criterion[] restrictions) {
        final Criteria c = getSession().createCriteria(persistentClass);
        if (restrictions != null) {
            for (Criterion criterion : restrictions) {
                c.add(criterion);
            }
        }
        return c;
    }

    public Session getSession() {
        try {
            return sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            return sessionFactory.openSession();
        }
    }

    public IdTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(IdTransformer transformer) {
        this.transformer = transformer;
    }

    public abstract Serializable getId(Object object);
}