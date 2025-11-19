package jpaoletti.jpm2.core.search.jpa;

import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.JPASearchResult;

/**
 * JPA Criteria API implementation of boolean searcher.
 *
 * @author jpaoletti
 */
public class BoolJPASearcher implements ISearcher {

    private String label = "jpm.searcher.boolSearcher.label";

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:bool-searcher.jsp?label=" + getLabel();
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final boolean value = parameters.get("value") != null && !"false".equals(parameters.get("value")[0]);
        final String searchProperty = JPASearcherHelper.getSearchProperty(field);

        final Message description = value ?
            MessageFactory.info("jpm.searcher.boolSearcher.eq.true") :
            MessageFactory.info("jpm.searcher.boolSearcher.eq.false");

        JPASearchResult result = new JPASearchResult(description, (cb, root) ->
            cb.equal(root.get(searchProperty), value)
        );

        return JPASearcherHelper.addAliases(result, field);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
