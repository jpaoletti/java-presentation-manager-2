package jpaoletti.jpm2.web.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.ListFilter;
import jpaoletti.jpm2.core.search.Searcher;
import jpaoletti.jpm2.core.search.SearcherHelper;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
public class ObjectSearcher implements Searcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.objectSearcher";

    private Entity entity;
    private ListFilter filter;
    private String textField;

    @Autowired
    private HttpServletRequest request;

    @Override
    public String visualization(Entity entity, Field field) {
        final StringBuilder sb = new StringBuilder("@page:object-searcher.jsp");
        sb.append("?entityId=").append(getEntity().getId());
        sb.append("&textField=").append(getTextField());
        sb.append("&field=").append(field.getId());
        if (getFilter() != null) {
            sb.append("&filter=").append(getFilter().getId());
        }
        return sb.toString();
    }

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String[] values = parameters.get("value");
        final List<Object> result = new ArrayList<>();
        for (String value : values) {
            result.add(getEntity().getDao().get(value));
        }
        return new DescribedCriterion(MessageFactory.info(DESCRIPTION_KEY, String.valueOf(result)), Restrictions.in(SearcherHelper.getSearchProperty(field), result));
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public ListFilter getFilter() {
        return filter;
    }

    public void setFilter(ListFilter filter) {
        this.filter = filter;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

}
