package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class IntegerSearcher implements Searcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.integerSearcher";

    @Override
    public String visualization() {
        return "@page:number-searcher.jsp";
    }

    @Override
    public DescribedCriterion build(Field field, Map<String, String[]> parameters) {
        final Object value = getValue(parameters);
        final String operator = parameters.get("operator")[0];
        switch (operator) {
            case "ne":
                return new DescribedCriterion(
                        MessageFactory.info(DESCRIPTION_KEY, "!=", String.valueOf(value)),
                        Restrictions.ne(field.getProperty(), value));
            case ">":
                return new DescribedCriterion(
                        MessageFactory.info(DESCRIPTION_KEY, ">", String.valueOf(value)),
                        Restrictions.gt(field.getProperty(), value));
            case ">=":
                return new DescribedCriterion(
                        MessageFactory.info(DESCRIPTION_KEY, ">=", String.valueOf(value)),
                        Restrictions.ge(field.getProperty(), value));
            case "<":
                return new DescribedCriterion(
                        MessageFactory.info(DESCRIPTION_KEY, "<", String.valueOf(value)),
                        Restrictions.lt(field.getProperty(), value));
            case "<=":
                return new DescribedCriterion(
                        MessageFactory.info(DESCRIPTION_KEY, "<=", String.valueOf(value)),
                        Restrictions.le(field.getProperty(), value));
            default:
                return new DescribedCriterion(
                        MessageFactory.info(DESCRIPTION_KEY, "=", String.valueOf(value)),
                        Restrictions.eq(field.getProperty(), value));
        }
    }

    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        return Integer.parseInt(parameters.get("value")[0]);
    }
}
