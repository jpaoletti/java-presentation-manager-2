package jpaoletti.jpm2.web.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Path;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.IdentifiableListFilter;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.JPASearchResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * JPA Criteria API implementation of {@link ObjectSearcher}.
 *
 * Same visualization (object picker via {@code object-searcher.jsp}) and value
 * parsing as {@link ObjectSearcher}, but builds a {@link JPASearchResult}
 * (predicate) instead of a Hibernate Criterion, so it works with JPA-based DAOs
 * ({@code JPADAO}).
 *
 * The selected ids are resolved to entity instances through the referenced
 * entity's DAO and matched with an {@code IN} predicate over the field's
 * property. Nested properties (e.g. {@code a.b}) are navigated with chained
 * {@code get()} calls, which create the necessary implicit joins.
 *
 * @author jpaoletti
 */
public class ObjectJPASearcher implements ISearcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.objectSearcher";

    private Entity entity;
    private IdentifiableListFilter filter;
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
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final String[] values = parameters.get("value");
        final List<Object> result = new ArrayList<>();
        for (String value : values) {
            result.add(getEntity().getDao().get(value));
        }
        final String[] path = field.getProperty().split("[.]");
        return new JPASearchResult(
                MessageFactory.info(DESCRIPTION_KEY, String.valueOf(result)),
                (cb, root) -> {
                    Path p = root;
                    for (String part : path) {
                        p = p.get(part);
                    }
                    return p.in(result);
                });
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public IdentifiableListFilter getFilter() {
        return filter;
    }

    public void setFilter(IdentifiableListFilter filter) {
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
