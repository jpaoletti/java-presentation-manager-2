package jpaoletti.jpm2.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Restrictions;

/**
 * known bug: Does not work with multiple filters of same field.
 *
 * @author jpaoletti
 */
public class CollectionSearcher2 implements Searcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.collectionSearcher2";

    private Entity entity;
    private String textField;
    private String filter;

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        final List<Object> values = getValues(entity, field, parameters);
        return SearcherHelper.addAliases(new DescribedCriterion(
            MessageFactory.info(DESCRIPTION_KEY, String.valueOf(values)),
            Restrictions.in(field.getProperty(), values)), field);
    }

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:collection-searcher2.jsp"
            + "?entityId=" + getEntity().getId()
            + "&field=" + field.getId()
            + "&textField=" + getTextField()
            + (getFilter() != null ? "&filter=" + getFilter() : "");
    }

    protected List<Object> getValues(Entity entity, Field field, Map<String, String[]> parameters) throws NumberFormatException {
        final String[] values = parameters.get("value");
        final List<Object> result = new ArrayList<>();
        for (String value : values) {
            result.add(getEntity().getDao().get(value));
        }
        return result;
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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

}
