package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;

/**
 * Adapter that wraps legacy Searcher implementations to work with the new ISearcher interface.
 * This allows existing Hibernate-based searchers to be used without modification.
 *
 * Usage:
 * <pre>
 * Searcher oldSearcher = new StringSearcher();
 * ISearcher newSearcher = new SearcherAdapter(oldSearcher);
 * </pre>
 *
 * @author jpaoletti
 */
public class SearcherAdapter implements ISearcher {

    private final Searcher legacySearcher;

    /**
     * Creates an adapter for a legacy Searcher
     *
     * @param legacySearcher the legacy searcher to wrap
     */
    public SearcherAdapter(Searcher legacySearcher) {
        this.legacySearcher = legacySearcher;
    }

    @Override
    public String visualization(Entity entity, Field field) {
        return legacySearcher.visualization(entity, field);
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        Searcher.DescribedCriterion describedCriterion = legacySearcher.build(entity, field, parameters);
        return new HibernateSearchResult(describedCriterion);
    }

    /**
     * Gets the wrapped legacy searcher
     *
     * @return the legacy searcher
     */
    public Searcher getLegacySearcher() {
        return legacySearcher;
    }
}
