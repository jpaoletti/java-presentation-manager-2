package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jpaoletti
 * @param <T>
 * @param <ID>
 */
public interface DAO<T, ID extends Serializable> {

    /**
     * Builds a new list configuration appropriate for this DAO implementation.
     * HibernateCriteriaDAO will return DAOListConfiguration, JPADAO will return JPADAOListConfiguration.
     *
     * @return a new instance of the appropriate IDAOListConfiguration implementation
     */
    public IDAOListConfiguration build();

    public Long count(IDAOListConfiguration configuration);

    public void delete(Object object);

    public T get(String id);

    /**
     * Search for one item matching the configuration
     *
     * @param configuration search configuration
     * @return the first item matching the configuration
     */
    public T find(IDAOListConfiguration configuration);

    public ID getId(Object object);

    public List<T> list(IDAOListConfiguration configuration);

    public void save(Object object);

    public void update(final Object object);

    public void detach(Object object);
}
