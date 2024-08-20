package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class StringInListSearcher implements Searcher {

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:string-inlist-searcher.jsp";
    }

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String value = parameters.get("value")[0];
        final Message info = MessageFactory.info("jpm.searcher.stringSearcher.like", value);
        final String fieldAlias = field.getId() + "sifs1";
        DescribedCriterion describedCriterion = new DescribedCriterion(info, Restrictions.in(fieldAlias + ".elements", value));
        SearcherHelper.addAliases(describedCriterion, field);
        describedCriterion.addAlias(SearcherHelper.getSearchProperty(field), fieldAlias);
        return SearcherHelper.addAliases(describedCriterion, field);
    }
}
