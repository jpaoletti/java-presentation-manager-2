package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private List<SearchCriteriaField> definitions; //Individual criterias

    public SearchCriteria() {
        this.definitions = new ArrayList<>();
    }

    public Criterion getCriterion() {
        return criterion;
    }

    /**
     * Do not add definitions directly.
     */
    public List<SearchCriteriaField> getDefinitions() {
        return definitions;
    }

    public void addDefinition(String fieldId, Searcher.DescribedCriterion describedCriterion) {
        getDefinitions().add(new SearchCriteriaField(fieldId, describedCriterion));
        addCriterion(describedCriterion.getCriterion());
    }

    public void removeDefinition(Integer i) {
        getDefinitions().remove(i.intValue());
        //Resets criterion
        this.criterion = null;
        for (SearchCriteriaField d : getDefinitions()) {
            addCriterion(d.getCriterion());
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
        private Criterion criterion;
        private Message description;

        public SearchCriteriaField(String fieldId, DescribedCriterion describedCriterion) {
            this.fieldId = fieldId;
            this.criterion = describedCriterion.getCriterion();
            this.description = describedCriterion.getDescription();
        }

        public String getFieldId() {
            return fieldId;
        }

        public void setFieldId(String fieldId) {
            this.fieldId = fieldId;
        }

        public Criterion getCriterion() {
            return criterion;
        }

        public void setCriterion(Criterion criterion) {
            this.criterion = criterion;
        }

        public Message getDescription() {
            return description;
        }

        public void setDescription(Message description) {
            this.description = description;
        }
    }
}
