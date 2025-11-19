package jpaoletti.jpm2.core.search.jpa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * JPA Criteria API implementation of date searcher.
 * Supports operators: =, !=, >, >=, <, <=
 *
 * @author jpaoletti
 */
public class DateJPASearcher implements ISearcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.dateSearcher";
    private String dateFormat = "dd/MM/yyyy";

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:date-searcher.jsp";
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        try {
            final Date value = getValue(parameters);
            if (value != null) {
                final String operator = parameters.get("operator")[0];
                final String searchProperty = getProperty(field);
                final SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
                final String formattedDate = sdf.format(value);
                final Message description = MessageFactory.info(getDescriptionKey(), operator, formattedDate);

                JPASearchResult result;
                switch (operator) {
                    case "ne": // NOT EQUAL
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.notEqual(root.get(searchProperty), value)
                        );
                        break;

                    case ">": // GREATER THAN (after)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.greaterThan(root.get(searchProperty), value)
                        );
                        break;

                    case ">=": // GREATER THAN OR EQUAL (on or after)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.greaterThanOrEqualTo(root.get(searchProperty), value)
                        );
                        break;

                    case "<": // LESS THAN (before)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.lessThan(root.get(searchProperty), value)
                        );
                        break;

                    case "<=": // LESS THAN OR EQUAL (on or before)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.lessThanOrEqualTo(root.get(searchProperty), value)
                        );
                        break;

                    case "eq": // EQUAL (explicit)
                    default: // EQUAL (default)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.equal(root.get(searchProperty), value)
                        );
                }

                return JPASearcherHelper.addAliases(result, field);
            } else {
                return null;
            }
        } catch (ParseException e) {
            return null;
        }
    }

    protected String getDescriptionKey() {
        return DESCRIPTION_KEY;
    }

    protected String getProperty(Field field) {
        return JPASearcherHelper.getSearchProperty(field);
    }

    protected Date getValue(Map<String, String[]> parameters) throws ParseException {
        final String v = parameters.get("value")[0];
        if (StringUtils.isEmpty(v)) {
            return null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
            sdf.setLenient(false);
            return sdf.parse(v);
        }
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
