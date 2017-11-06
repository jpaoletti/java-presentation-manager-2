package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class StringSearcher implements Searcher {

    @Override
    public String visualization(Field field) {
        return "@page:string-searcher.jsp";
    }

    @Override
    public DescribedCriterion build(Field field, Map<String, String[]> parameters) {
        final String value = parameters.get("value")[0];
        final String operator = ((String[]) parameters.get("operator"))[0];
        DescribedCriterion describedCriterion;
        switch (operator) {
            case "li":
                describedCriterion = new DescribedCriterion(MessageFactory.info("jpm.searcher.stringSearcher.like", value), Restrictions.ilike(field.getProperty(), value, MatchMode.ANYWHERE));
                break;
            case "null":
                describedCriterion = new DescribedCriterion(MessageFactory.info("jpm.searcher.stringSearcher.null"), Restrictions.isNull(field.getProperty()));
                break;
            default:
                describedCriterion = new DescribedCriterion(MessageFactory.info("jpm.searcher.stringSearcher.eq", value), Restrictions.eq(field.getProperty(), value));
        }
        return SearcherHelper.addAliases(describedCriterion, field);
    }
}
