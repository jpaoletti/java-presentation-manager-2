package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class StringSearcher implements Searcher {

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:string-searcher.jsp";
    }

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String value = parameters.get("value")[0];
        final String operator = ((String[]) parameters.get("operator"))[0];
        final Message info = MessageFactory.info("jpm.searcher.stringSearcher." + operator, value);
        DescribedCriterion describedCriterion;
        switch (operator) {
            case "li":
                describedCriterion = new DescribedCriterion(info, Restrictions.ilike(SearcherHelper.getSearchProperty(field), value, MatchMode.ANYWHERE));
                break;
            case "nli":
                describedCriterion = new DescribedCriterion(info, Restrictions.not(Restrictions.ilike(SearcherHelper.getSearchProperty(field), value, MatchMode.ANYWHERE)));
                break;
            case "ne":
                describedCriterion = new DescribedCriterion(info, Restrictions.ne(SearcherHelper.getSearchProperty(field), value));
                break;
            case "null":
                describedCriterion = new DescribedCriterion(info, Restrictions.isNull(SearcherHelper.getSearchProperty(field)));
                break;
            default:
                describedCriterion = new DescribedCriterion(info, Restrictions.eq(SearcherHelper.getSearchProperty(field), value));
        }
        return SearcherHelper.addAliases(describedCriterion, field);
    }
}
