package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;

/**
 * Generic searcher interface that returns ISearchResult.
 * This interface allows searchers to work with both Hibernate and JPA implementations.
 *
 * Implementations can return either HibernateSearchResult or JPASearchResult.
 *
 * @author jpaoletti
 */
public interface ISearcher {

    /**
     * Returns the visualization JSP page for this searcher's UI
     *
     * @param entity the entity
     * @param field the field
     * @return the JSP page path (e.g., "@page:string-searcher.jsp")
     */
    String visualization(Entity entity, Field field);

    /**
     * Builds a search result from the given parameters.
     * Implementations can return either HibernateSearchResult (for Hibernate DAOs)
     * or JPASearchResult (for JPA DAOs).
     *
     * @param entity the entity
     * @param field the field to search on
     * @param parameters the search parameters from the request
     * @return an ISearchResult that can be applied to any DAO configuration
     */
    ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters);
}
