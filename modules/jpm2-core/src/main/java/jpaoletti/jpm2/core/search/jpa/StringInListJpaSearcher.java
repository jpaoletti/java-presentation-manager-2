package jpaoletti.jpm2.core.search.jpa;

import java.util.Map;
import javax.persistence.criteria.JoinType;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.JPASearchResult;

/**
 * JPA Criteria API implementation of StringInListSearcher.
 * Searches for a value within an @ElementCollection of strings using a join.
 *
 * @author jpaoletti
 */
public class StringInListJpaSearcher implements ISearcher {

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:string-inlist-searcher.jsp";
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String value = parameters.get("value")[0];
        final Message info = MessageFactory.info("jpm.searcher.stringSearcher.like", value);
        final String property = JPASearcherHelper.getSearchProperty(field);

        final JPASearchResult result = new JPASearchResult(info, (cb, root) -> {
            final var join = root.join(property, JoinType.INNER);
            return cb.equal(join, value);
        });

        return result;
    }
}
