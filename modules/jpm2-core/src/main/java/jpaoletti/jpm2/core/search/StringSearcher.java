package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class StringSearcher implements Searcher {

    @Override
    public String visualization() {
        return "@page:string-searcher.jsp";
    }

    @Override
    public Criterion build(Field field, Map<String, Object> parameters) {
        final String value = ((String[]) parameters.get("value"))[0];
        final String operator = ((String[]) parameters.get("operator"))[0];
        switch (operator) {
            case "li":
                return Restrictions.like(field.getProperty(), value, MatchMode.ANYWHERE);
            default:
                return Restrictions.eq(field.getProperty(), value);
        }
    }
}
