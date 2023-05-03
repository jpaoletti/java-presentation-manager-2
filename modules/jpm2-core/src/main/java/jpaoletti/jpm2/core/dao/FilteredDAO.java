package jpaoletti.jpm2.core.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Superclass for DAO with some restrictions.
 *
 * @author jpaoletti
 */
public abstract class FilteredDAO extends DefaultDAO {

    @Override
    public Object get(String id) {
        try {
            Criteria c = getSession().createCriteria(getPersistentClass()).add(Restrictions.eq("id", Long.parseLong(id)));
            addRestrictions(c, null);
            return c.uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Criteria getBaseCriteria(IDAOListConfiguration configuration) {
        final Criteria c = super.getBaseCriteria(configuration);
        addRestrictions(c, configuration);
        return c;
    }

    public abstract void addRestrictions(Criteria c, IDAOListConfiguration configuration);

}
