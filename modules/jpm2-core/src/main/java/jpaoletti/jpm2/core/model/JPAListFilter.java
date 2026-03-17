package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.dao.IDAOListConfiguration;

/**
 * Optional filter for JPADAOListConfiguration-based listing.
 */
public interface JPAListFilter {

    void applyFilter(IDAOListConfiguration dlc, Entity entity, SessionEntityData sed, String currentId, String owner, String ownerId, String relatedValue);
}
