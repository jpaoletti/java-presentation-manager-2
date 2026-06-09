package jpaoletti.jpm2.core.search;

import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;

/**
 * JPA Criteria API implementation of CollectionSearcher3.
 * Same as CollectionSearcher2JPA but uses the collection-searcher3.jsp visualization.
 */
public class CollectionSearcher3JPA extends CollectionSearcher2JPA {

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:collection-searcher3.jsp"
                + "?entityId=" + getEntity().getId()
                + "&field=" + field.getId()
                + "&textField=" + getTextField()
                + (getFilter() != null ? "&filter=" + getFilter() : "");
    }
}
