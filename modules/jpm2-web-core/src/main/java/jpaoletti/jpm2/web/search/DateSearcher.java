package jpaoletti.jpm2.web.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.Searcher;
import jpaoletti.jpm2.core.search.SearcherHelper;
import jpaoletti.jpm2.util.JPMUtils;
import static jpaoletti.jpm2.web.converter.WebEditDate.RFC3339;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author jpaoletti
 */
public class DateSearcher implements Searcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.dateSearcher";
    private String format = "yyyy-MM-dd";

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:date-searcher.jsp?format=" + getFormat();
    }

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        try {
            final Date value = getValue(parameters);
            final SimpleDateFormat sdf = new SimpleDateFormat(getFormat());
            final String operator = parameters.get("operator")[0];
            DescribedCriterion describedCriterion = null;
            switch (operator) {
                case "ne":
                    describedCriterion = new DescribedCriterion(
                            MessageFactory.info(DESCRIPTION_KEY, "!=", sdf.format(value)),
                            Restrictions.ne(SearcherHelper.getSearchProperty(field), value));
                    break;
                case ">":
                    describedCriterion = new DescribedCriterion(
                            MessageFactory.info(DESCRIPTION_KEY, ">", sdf.format(value)),
                            Restrictions.gt(SearcherHelper.getSearchProperty(field), value));
                    break;
                case ">=":
                    describedCriterion = new DescribedCriterion(
                            MessageFactory.info(DESCRIPTION_KEY, ">=", sdf.format(value)),
                            Restrictions.ge(SearcherHelper.getSearchProperty(field), value));
                    break;
                case "<":
                    describedCriterion = new DescribedCriterion(
                            MessageFactory.info(DESCRIPTION_KEY, "<", sdf.format(value)),
                            Restrictions.lt(SearcherHelper.getSearchProperty(field), value));
                    break;
                case "<=":
                    describedCriterion = new DescribedCriterion(
                            MessageFactory.info(DESCRIPTION_KEY, "<=", sdf.format(value)),
                            Restrictions.le(SearcherHelper.getSearchProperty(field), value));
                    break;
                default:
                    describedCriterion = new DescribedCriterion(
                            MessageFactory.info(DESCRIPTION_KEY, "=", sdf.format(value)),
                            Restrictions.eq(SearcherHelper.getSearchProperty(field), value));
                    break;
            }
            return SearcherHelper.addAliases(describedCriterion, field);
        } catch (ParseException ex) {
            JPMUtils.getLogger().error("Error on DateSearcher", ex);
            return null;
        }
    }

    protected Date getValue(Map<String, String[]> parameters) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat(RFC3339);
        //final SimpleDateFormat sdf = new SimpleDateFormat(getFormat());
        return sdf.parse(parameters.get("value")[0]);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
