package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.search.Searcher;
import jpaoletti.jpm2.core.search.Searcher.DescribedCriterion;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * List search criteria.
 *
 * @author jpaoletti
 */
public class SearchCriteria implements Serializable {

    private Criterion criterion; //Global criteria search, Something like Restrictions.and(... , ...)
    private final List<SearchCriteriaField> definitions; //Individual criterias
    private final Set<DAOListConfiguration.DAOListConfigurationAlias> aliases;

    public SearchCriteria() {
        this.definitions = new ArrayList<>();
        this.aliases = new LinkedHashSet<>();
    }

    public void clear() {
        getDefinitions().clear();
        getAliases().clear();
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

    public void addDefinition(String fieldId, Searcher.DescribedCriterion describedCriterion) {
        getDefinitions().add(new SearchCriteriaField(fieldId, describedCriterion));
        getAliases().addAll(describedCriterion.getAliases());
        addCriterion(describedCriterion.getCriterion());
    }

    public void removeDefinition(Integer i) {
        try {
            getDefinitions().remove(i.intValue());
        } catch (Exception e) {
            //just in case
        }
        //Resets criterion
        this.criterion = null;
        for (SearchCriteriaField d : getDefinitions()) {
            addCriterion(d.getDescribedCriterion().getCriterion());
            getAliases().addAll(d.getDescribedCriterion().getAliases());
        }
    }

    protected void addCriterion(Criterion c) {
        if (getCriterion() == null) {
            this.criterion = c;
        } else {
            this.criterion = Restrictions.and(getCriterion(), c);
        }
    }

    public static class SearchCriteriaField {

        private String fieldId;
        private DescribedCriterion describedCriterion;

        public SearchCriteriaField(String fieldId, DescribedCriterion describedCriterion) {
            this.fieldId = fieldId;
            this.describedCriterion = describedCriterion;
        }

        public Message getDescription() {
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
    }
}
