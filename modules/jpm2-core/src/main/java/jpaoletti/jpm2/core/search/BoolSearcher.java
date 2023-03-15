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
public class BoolSearcher implements Searcher {

    private String label = "jpm.searcher.boolSearcher.label";

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:bool-searcher.jsp?label=" + getLabel();
    }

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        final boolean value = parameters.get("value") != null && !"false".equals(parameters.get("value")[0]);
        return SearcherHelper.addAliases(new DescribedCriterion(
                value ? MessageFactory.info("jpm.searcher.boolSearcher.eq.true") : MessageFactory.info("jpm.searcher.boolSearcher.eq.false"),
                Restrictions.eq(SearcherHelper.getSearchProperty(field), value)), field);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
