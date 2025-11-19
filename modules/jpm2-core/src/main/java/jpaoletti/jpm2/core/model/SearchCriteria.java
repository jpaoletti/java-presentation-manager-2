package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.IDAOListConfiguration;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.Searcher;
import jpaoletti.jpm2.core.search.Searcher.DescribedCriterion;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * List search criteria.
 * Now supports both Hibernate Criteria (legacy) and JPA Criteria (new) via ISearchResult.
 *
 * @author jpaoletti
 */
public class SearchCriteria implements Serializable {

    private Criterion criterion; //Global criteria search, Something like Restrictions.and(... , ...) - LEGACY
    private final List<SearchCriteriaField> definitions; //Individual criterias
    private final Set<DAOListConfiguration.DAOListConfigurationAlias> aliases; //LEGACY aliases
    private final List<ISearchResult> searchResults; //NEW: Generic search results

    public SearchCriteria() {
        this.definitions = new ArrayList<>();
        this.aliases = new LinkedHashSet<>();
        this.searchResults = new ArrayList<>();
    }

    public void clear() {
        this.criterion = null;
        getDefinitions().clear();
        getAliases().clear();
        getSearchResults().clear();
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public Set<DAOListConfiguration.DAOListConfigurationAlias> getAliases() {
        return aliases;
    }

    /**
     * Do not add definitions directly.
     *
     * @return
     */
    public List<SearchCriteriaField> getDefinitions() {
        return definitions;
    }

    /**
     * Adds a search definition using legacy DescribedCriterion (for backward compatibility)
     *
     * @param fieldId the field ID
     * @param describedCriterion the described criterion
     */
    public void addDefinition(String fieldId, Searcher.DescribedCriterion describedCriterion) {
        getDefinitions().add(new SearchCriteriaField(fieldId, describedCriterion));
        getAliases().addAll(describedCriterion.getAliases());
        addCriterion(describedCriterion.getCriterion());
    }

    /**
     * Adds a generic search result (new method for ISearchResult support)
     *
     * @param fieldId the field ID
     * @param searchResult the search result (can be HibernateSearchResult or JPASearchResult)
     */
    public void addSearchResult(String fieldId, ISearchResult searchResult) {
        getDefinitions().add(new SearchCriteriaField(fieldId, searchResult));
        getSearchResults().add(searchResult);
    }

    public void removeDefinition(String fieldId) {
        int i = 0;
        Integer toRemove = null;
        for (SearchCriteriaField definition : getDefinitions()) {
            if (definition.getFieldId().equalsIgnoreCase(fieldId)) {
                toRemove = i;
                break;
            }
            i++;
        }
        if (toRemove != null) {
            removeDefinition(i);
        }
    }

    public void removeDefinition(Integer i) {
        try {
            getDefinitions().remove(i.intValue());
        } catch (Exception e) {
            //just in case
        }
        //Resets criterion and searchResults
        this.criterion = null;
        getAliases().clear();
        getSearchResults().clear();

        // Rebuild both Hibernate criteria and JPA search results
        for (SearchCriteriaField d : getDefinitions()) {
            // Handle Hibernate (legacy)
            if (d.getDescribedCriterion() != null) {
                addCriterion(d.getDescribedCriterion().getCriterion());
                getAliases().addAll(d.getDescribedCriterion().getAliases());
            }
            // Handle JPA (modern)
            if (d.getSearchResult() != null) {
                getSearchResults().add(d.getSearchResult());
            }
        }
    }

    protected void addCriterion(Criterion c) {
        if (getCriterion() == null) {
            this.criterion = c;
        } else {
            this.criterion = Restrictions.and(getCriterion(), c);
        }
    }

    /**
     * Gets the list of generic search results
     *
     * @return the search results
     */
    public List<ISearchResult> getSearchResults() {
        return searchResults;
    }

    /**
     * Applies all search results to the given configuration.
     * This is the new way to apply searches, works with both Hibernate and JPA configurations.
     *
     * @param configuration the DAO list configuration
     */
    public void applyTo(IDAOListConfiguration configuration) {
        for (ISearchResult searchResult : getSearchResults()) {
            searchResult.applyTo(configuration);
        }
    }

    /**
     * Checks if there are any search results
     *
     * @return true if there are search results
     */
    public boolean hasSearchResults() {
        return !searchResults.isEmpty();
    }

    public static class SearchCriteriaField {

        private String fieldId;
        private DescribedCriterion describedCriterion;
        private ISearchResult searchResult;

        public SearchCriteriaField(String fieldId, DescribedCriterion describedCriterion) {
            this.fieldId = fieldId;
            this.describedCriterion = describedCriterion;
        }

        public SearchCriteriaField(String fieldId, ISearchResult searchResult) {
            this.fieldId = fieldId;
            this.searchResult = searchResult;
        }

        public Message getDescription() {
            if (searchResult != null) {
                return searchResult.getDescription();
            }
            return getDescribedCriterion().getDescription();
        }

        public String getFieldId() {
            return fieldId;
        }

        public void setFieldId(String fieldId) {
            this.fieldId = fieldId;
        }

        public DescribedCriterion getDescribedCriterion() {
            return describedCriterion;
        }

        public void setDescribedCriterion(DescribedCriterion describedCriterion) {
            this.describedCriterion = describedCriterion;
        }

        public ISearchResult getSearchResult() {
            return searchResult;
        }

        public void setSearchResult(ISearchResult searchResult) {
            this.searchResult = searchResult;
        }
    }
}

