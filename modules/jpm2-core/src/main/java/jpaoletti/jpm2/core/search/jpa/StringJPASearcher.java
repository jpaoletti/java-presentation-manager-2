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
 * JPA Criteria API implementation of string searcher.
 * This is an example of how to implement searchers for JPA-based DAOs.
 *
 * This searcher works with JPADAOListConfiguration and uses predicates
 * instead of Hibernate Criteria.
 *
 * @author jpaoletti
 */
public class StringJPASearcher implements ISearcher {

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:string-searcher.jsp";
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String value = parameters.get("value")[0];
        final String operator = parameters.get("operator")[0];
        final Message info = MessageFactory.info("jpm.searcher.stringSearcher." + operator, value);

        final String searchProperty = JPASearcherHelper.getSearchProperty(field);
        JPASearchResult result;

        switch (operator) {
            case "li": // LIKE (case insensitive)
                result = new JPASearchResult(info, (cb, root) ->
                    cb.like(cb.lower(root.get(searchProperty)), "%" + value.toLowerCase() + "%")
                );
                break;

            case "nli": // NOT LIKE (case insensitive)
                result = new JPASearchResult(info, (cb, root) ->
                    cb.not(cb.like(cb.lower(root.get(searchProperty)), "%" + value.toLowerCase() + "%"))
                );
                break;

            case "ne": // NOT EQUAL
                result = new JPASearchResult(info, (cb, root) ->
                    cb.notEqual(root.get(searchProperty), value)
                );
                break;

            case "null": // IS NULL
                result = new JPASearchResult(info, (cb, root) ->
                    cb.isNull(root.get(searchProperty))
                );
                break;

            default: // EQUAL
                result = new JPASearchResult(info, (cb, root) ->
                    cb.equal(root.get(searchProperty), value)
                );
        }

        return JPASearcherHelper.addAliases(result, field);
    }
}
