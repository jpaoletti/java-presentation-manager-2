package jpaoletti.jpm2.core.search;

import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;

/**
 * known bug: Does not work with multiple filters of same field.
 *
 * @author jpaoletti
 */
public class CollectionSearcher3 extends CollectionSearcher2 {

    @Override
    public String visualization(Entity entity, Field field) {
        return "@page:collection-searcher3.jsp"
                + "?entityId=" + getEntity().getId()
                + "&field=" + field.getId()
                + "&textField=" + getTextField()
                + (getFilter() != null ? "&filter=" + getFilter() : "");
    }

}
