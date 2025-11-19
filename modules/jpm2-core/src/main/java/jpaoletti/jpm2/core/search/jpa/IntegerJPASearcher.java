package jpaoletti.jpm2.core.search.jpa;

import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.JPASearchResult;
import org.apache.commons.lang3.StringUtils;

/**
 * JPA Criteria API implementation of integer searcher.
 * Supports operators: =, !=, >, >=, <, <=
 *
 * @author jpaoletti
 */
public class IntegerJPASearcher implements ISearcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.integerSearcher";

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:number-searcher.jsp";
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        try {
            final Object value = getValue(parameters);
            if (value != null) {
                final String operator = parameters.get("operator")[0];
                final String searchProperty = getProperty(field);
                final Message description = MessageFactory.info(getDescriptionKey(), operator, String.valueOf(value));

                JPASearchResult result;
                switch (operator) {
                    case "ne": // NOT EQUAL
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.notEqual(root.get(searchProperty), value)
                        );
                        break;

                    case ">": // GREATER THAN
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.gt(root.get(searchProperty), (Number) value)
                        );
                        break;

                    case ">=": // GREATER THAN OR EQUAL
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.ge(root.get(searchProperty), (Number) value)
                        );
                        break;

                    case "<": // LESS THAN
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.lt(root.get(searchProperty), (Number) value)
                        );
                        break;

                    case "<=": // LESS THAN OR EQUAL
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.le(root.get(searchProperty), (Number) value)
                        );
                        break;

                    default: // EQUAL
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.equal(root.get(searchProperty), value)
                        );
                }

                return JPASearcherHelper.addAliases(result, field);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected String getDescriptionKey() {
        return DESCRIPTION_KEY;
    }

    protected String getProperty(Field field) {
        return JPASearcherHelper.getSearchProperty(field);
    }

    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        final String v = parameters.get("value")[0];
        if (StringUtils.isEmpty(v)) {
            return null;
        } else {
            return Integer.parseInt(v);
        }
    }
}
