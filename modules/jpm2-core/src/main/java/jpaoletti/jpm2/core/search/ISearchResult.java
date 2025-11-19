package jpaoletti.jpm2.core.search;

import jpaoletti.jpm2.core.dao.IDAOListConfiguration;
import jpaoletti.jpm2.core.message.Message;

/**
 * Interface that represents a search result that can be applied to any DAO configuration.
 * This allows flexibility to support both Hibernate Criteria (DAOListConfiguration)
 * and JPA Criteria API (JPADAOListConfiguration).
 *
 * @author jpaoletti
 */
public interface ISearchResult {

    /**
     * Gets the description message for this search filter
     *
     * @return the description message
     */
    Message getDescription();

    /**
     * Applies this search result to the given configuration.
     * The implementation will detect the configuration type and apply the appropriate filter.
     *
     * @param configuration the DAO list configuration (DAOListConfiguration or JPADAOListConfiguration)
     */
    void applyTo(IDAOListConfiguration configuration);

    /**
     * Checks if this search result has any aliases that need to be added
     *
     * @return true if there are aliases
     */
    boolean hasAliases();
}
