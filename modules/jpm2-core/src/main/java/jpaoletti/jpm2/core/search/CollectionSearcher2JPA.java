package jpaoletti.jpm2.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;

/**
 * JPA Criteria API implementation of CollectionSearcher2.
 * Filters by membership in a related entity collection using IN predicate.
 */
public class CollectionSearcher2JPA implements ISearcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.collectionSearcher2";

    private Entity entity;
    private String textField;
    private String filter;

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:collection-searcher2.jsp"
                + "?entityId=" + getEntity().getId()
                + "&field=" + field.getId()
                + "&textField=" + getTextField()
                + (getFilter() != null ? "&filter=" + getFilter() : "");
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final List<Object> values = getValues(parameters);
        // El campo es una coleccion (ej. tags, groups): hay que JOINear la coleccion
        // y filtrar el elemento IN values. Usar root.get(coleccion).in(...) genera
        // SQL invalido ("No value specified for parameter 1").
        final String property = field.getProperty();
        return new JPASearchResult(
            MessageFactory.info(DESCRIPTION_KEY, String.valueOf(values)),
            (cb, root) -> {
                final String[] path = property.split("[.]");
                javax.persistence.criteria.From<?, ?> from = root;
                for (int i = 0; i < path.length - 1; i++) {
                    from = from.join(path[i]);
                }
                return from.join(path[path.length - 1]).in(values);
            }
        );
    }

    protected List<Object> getValues(Map<String, String[]> parameters) {
        final String[] rawValues = parameters.get("value");
        final List<Object> result = new ArrayList<>();
        for (String value : rawValues) {
            result.add(getEntity().getDao().get(value));
        }
        return result;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
