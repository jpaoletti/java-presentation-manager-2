package jpaoletti.jpm2.core.search.jpa;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.JPASearchResult;

/**
 * JPA Criteria API implementation of CollectionSearcher: string search over a
 * property ("searchField") of the elements of the field collection. Same
 * operators and page as the string searcher.
 *
 * @author jpaoletti
 */
public class CollectionJPASearcher implements ISearcher {

    private String searchField;

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:string-searcher.jsp";
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String value = parameters.get("value")[0];
        final String operator = parameters.get("operator")[0];
        final Message info = MessageFactory.info("jpm.searcher.stringSearcher." + operator, value);
        final String property = field.getProperty();
        return new JPASearchResult(info, (cb, root) -> predicate(cb, path(root, property), operator, value));
    }

    private Expression<String> path(Root root, String property) {
        From<?, ?> from = root;
        for (String part : property.split("[.]")) {
            from = from.join(part);
        }
        return from.get(getSearchField());
    }

    private static Predicate predicate(CriteriaBuilder cb, Expression<String> path, String operator, String value) {
        switch (operator) {
            case "li":
                return cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
            case "nli":
                return cb.not(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
            case "ne":
                return cb.notEqual(path, value);
            case "null":
                return cb.isNull(path);
            default:
                return cb.equal(path, value);
        }
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }
}
