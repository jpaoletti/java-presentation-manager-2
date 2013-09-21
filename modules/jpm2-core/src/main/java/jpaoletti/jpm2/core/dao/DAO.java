package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

/**
 *
 * @author jpaoletti
 */
public interface DAO<T, ID extends Serializable> {

    public Long count(Criterion... restrictions);

    public void delete(Object object);

    public T get(String id);

    public ID getId(Object object);

    public List<T> list(Integer from, Integer max, Order order, Criterion... restrictions);

    public void save(Object object);

    public void update(final Object object);
}
