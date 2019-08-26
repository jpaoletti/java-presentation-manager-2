package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class IntegerSearcher implements Searcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.integerSearcher";

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:number-searcher.jsp";
    }

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        try {
            final Object value = getValue(parameters);
            final String operator = parameters.get("operator")[0];
            switch (operator) {
                case "ne":
                    return SearcherHelper.addAliases(new DescribedCriterion(
                            MessageFactory.info(getDescriptionKey(), "!=", String.valueOf(value)),
                            Restrictions.ne(getProperty(field), value)), field);
                case ">":
                    return SearcherHelper.addAliases(new DescribedCriterion(
                            MessageFactory.info(getDescriptionKey(), ">", String.valueOf(value)),
                            Restrictions.gt(getProperty(field), value)), field);
                case ">=":
                    return SearcherHelper.addAliases(new DescribedCriterion(
                            MessageFactory.info(getDescriptionKey(), ">=", String.valueOf(value)),
                            Restrictions.ge(getProperty(field), value)), field);
                case "<":
                    return SearcherHelper.addAliases(new DescribedCriterion(
                            MessageFactory.info(getDescriptionKey(), "<", String.valueOf(value)),
                            Restrictions.lt(getProperty(field), value)), field);
                case "<=":
                    return SearcherHelper.addAliases(new DescribedCriterion(
                            MessageFactory.info(getDescriptionKey(), "<=", String.valueOf(value)),
                            Restrictions.le(getProperty(field), value)), field);
                default:
                    return SearcherHelper.addAliases(new DescribedCriterion(
                            MessageFactory.info(getDescriptionKey(), "=", String.valueOf(value)),
                            Restrictions.eq(getProperty(field), value)), field);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected String getDescriptionKey() {
        return DESCRIPTION_KEY;
    }

    protected String getProperty(Field field) {
        return field.getProperty();
    }

    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        return Integer.parseInt(parameters.get("value")[0]);
    }
}
