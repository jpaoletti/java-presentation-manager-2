package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class BoolSearcher implements Searcher {

    @Override
    public String visualization() {
        return "@page:bool-searcher.jsp";
    }

    @Override
    public Criterion build(Field field, Map<String, Object> parameters) {
        final String value = ((String[]) parameters.get("value"))[0];
        if (value != null) {
            return Restrictions.eq(field.getProperty(), value.equalsIgnoreCase("TRUE"));
        } else {
            return null;
        }

    }
}
