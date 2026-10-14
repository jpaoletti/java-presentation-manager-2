package jpaoletti.jpm2.core.menu;

import jpaoletti.jpm2.core.dao.IDAOListConfiguration;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.JPAListFilter;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.core.model.persistent.JpmMenuType;

/**
 * Only GROUP items, for the parent selector of the jpmMenu entity. The bean id
 * must match {@link #getId()}.
 *
 * @author jpaoletti
 */
public class JpmMenuGroupFilter implements JPAListFilter {

    @Override
    public void applyFilter(IDAOListConfiguration dlc, Entity entity, SessionEntityData sed, String currentId, String owner, String ownerId, String relatedValue) {
        ((JPADAOListConfiguration) dlc).withPredicate((cb, root) -> cb.equal(root.get("type"), JpmMenuType.GROUP));
    }

    @Override
    public String getId() {
        return "jpmMenuGroupFilter";
    }
}
