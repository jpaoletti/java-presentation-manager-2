package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * List search criteria.
 *
 * @author jpaoletti
 */
public class SearchCriteria {

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

    public void addDefinition(String fieldId, Criterion c) {
        getDefinitions().add(new SearchCriteriaField(fieldId, c));
        addCriterion(c);
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

        public SearchCriteriaField() {
        }

        public SearchCriteriaField(String fieldId, Criterion criterion) {
            this.fieldId = fieldId;
            this.criterion = criterion;
        }

        @Override
        public String toString() {
            return getCriterion().toString();
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
    }
}
