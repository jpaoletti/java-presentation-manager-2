package jpaoletti.jpm2.core.model;

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
     * @param entity
     * @param sessionData
     * @param ownerId
     * @return the filter object
     */
    public Criterion getListFilter(Entity entity, SessionEntityData sessionData, String ownerId);
}
