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
    /**
     * Format submitted by the HTML5 {@code <input type="date">} used in
     * date-searcher.jsp. This value is always ISO (yyyy-MM-dd) regardless of the
     * browser locale, so it must be used to parse the incoming value.
     */
    private static final String INPUT_FORMAT = "yyyy-MM-dd";
    /**
     * Format used only to render the human readable description of the applied
     * filter (not to parse the submitted value).
     */
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
                            cb.notEqual(JPASearcherHelper.path(root, searchProperty), value)
                        );
                        break;

                    case ">": // GREATER THAN (after)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.greaterThan(JPASearcherHelper.path(root, searchProperty), value)
                        );
                        break;

                    case ">=": // GREATER THAN OR EQUAL (on or after)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.greaterThanOrEqualTo(JPASearcherHelper.path(root, searchProperty), value)
                        );
                        break;

                    case "<": // LESS THAN (before)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.lessThan(JPASearcherHelper.path(root, searchProperty), value)
                        );
                        break;

                    case "<=": // LESS THAN OR EQUAL (on or before)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.lessThanOrEqualTo(JPASearcherHelper.path(root, searchProperty), value)
                        );
                        break;

                    case "eq": // EQUAL (explicit)
                    default: // EQUAL (default)
                        result = new JPASearchResult(description, (cb, root) ->
                            cb.equal(JPASearcherHelper.path(root, searchProperty), value)
                        );
                }

                return result;
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
        }
        // The <input type="date"> submits ISO (yyyy-MM-dd). Fall back to the
        // configured dateFormat for any view that submits localized text.
        try {
            return parse(INPUT_FORMAT, v);
        } catch (ParseException e) {
            return parse(getDateFormat(), v);
        }
    }

    private Date parse(String format, String value) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        return sdf.parse(value);
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
