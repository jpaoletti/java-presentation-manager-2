package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class IntegerSearcher implements Searcher {

    @Override
    public String visualization() {
        return "@page:number-searcher.jsp";
    }

    @Override
    public Criterion build(Field field, Map<String, String[]> parameters) {
        final Object value = getValue(parameters);
        final String operator = parameters.get("operator")[0];
        switch (operator) {
            case "ne":
                return Restrictions.ne(field.getProperty(), value);
            case ">":
                return Restrictions.gt(field.getProperty(), value);
            case ">=":
                return Restrictions.ge(field.getProperty(), value);
            case "<":
                return Restrictions.lt(field.getProperty(), value);
            case "<=":
                return Restrictions.le(field.getProperty(), value);
            default:
                return Restrictions.eq(field.getProperty(), value);
        }
    }

    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        return Integer.parseInt(parameters.get("value")[0]);
    }
}
