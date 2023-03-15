package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * known bug: Does not work with multiple filters of same field.
 *
 * @author jpaoletti
 */
public class CollectionSearcher extends StringSearcher {

    private String searchField;

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String value = parameters.get("value")[0];
        final String operator = ((String[]) parameters.get("operator"))[0];
        final Message info = MessageFactory.info("jpm.searcher.stringSearcher." + operator, value);
        final String aliasName = "alJpm" + System.currentTimeMillis();
        DescribedCriterion describedCriterion;
        final String property = aliasName + "." + getSearchField();
        switch (operator) {
            case "li":
                describedCriterion = new DescribedCriterion(info, Restrictions.ilike(property, value, MatchMode.ANYWHERE));
                break;
            case "nli":
                describedCriterion = new DescribedCriterion(info, Restrictions.not(Restrictions.ilike(property, value, MatchMode.ANYWHERE)));
                break;
            case "ne":
                describedCriterion = new DescribedCriterion(info, Restrictions.ne(property, value));
                break;
            case "null":
                describedCriterion = new DescribedCriterion(info, Restrictions.isNull(property));
                break;
            default:
                describedCriterion = new DescribedCriterion(info, Restrictions.eq(property, value));
        }
        describedCriterion.addAlias(SearcherHelper.getSearchProperty(field), aliasName);
        return SearcherHelper.addAliases(describedCriterion, field);
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

}
