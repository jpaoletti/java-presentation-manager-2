package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import org.hibernate.criterion.Criterion;

/**
 * An interface that filters the data shown by an entity.
 *
 * @author jpaoletti
 *
 */
public interface ListFilter {

    /**
     * Returns a unique id for the filter;
     *
     * @return
     */
    public String getId();

    /**
     * @param dcl Dao configuration list being configured
     * @param entity
     * @param sessionData
     * @param ownerId
     * @return the filter object
     */
    public Criterion getListFilter(final DAOListConfiguration dlc, Entity entity, SessionEntityData sessionData, String ownerId);
}
