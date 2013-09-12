package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Field;
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
    public DescribedCriterion build(Field field, Map<String, String[]> parameters) {
        final boolean value = parameters.get("value") != null;
        return new DescribedCriterion(
                value ? MessageFactory.info("jpm.searcher.boolSearcher.eq.true") : MessageFactory.info("jpm.searcher.boolSearcher.eq.false"),
                Restrictions.eq(field.getProperty(), value));
    }
}
