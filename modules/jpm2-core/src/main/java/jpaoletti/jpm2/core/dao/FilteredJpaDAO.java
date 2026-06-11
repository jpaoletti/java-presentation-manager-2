package jpaoletti.jpm2.core.dao;

import java.util.List;

/**
 * Superclass for JPA DAO with additional restrictions.
 *
 * @author jpaoletti
 */
public abstract class FilteredJpaDAO extends DefaultJpaDAO {

    @Override
    public Object get(String id) {
        if (id == null) {
            return null;
        }
        try {
            JPADAOListConfiguration configuration = withRestrictions(null);
            configuration.withPredicate((cb, root) -> cb.equal(root.get("id"), Long.valueOf(id)));
            return find(configuration);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object find(IDAOListConfiguration configuration) {
        return super.find(withRestrictions(configuration));
    }

    @Override
    public Long count(IDAOListConfiguration configuration) {
        return super.count(withRestrictions(configuration));
    }

    @Override
    public List<Object> list(IDAOListConfiguration configuration) {
        return super.list(withRestrictions(configuration));
    }

    protected JPADAOListConfiguration withRestrictions(IDAOListConfiguration configuration) {
        final JPADAOListConfiguration cfg = configuration instanceof JPADAOListConfiguration
                ? ((JPADAOListConfiguration) configuration).clone()
                : build();
        addRestrictions(cfg);
        return cfg;
    }

    public abstract void addRestrictions(JPADAOListConfiguration configuration);
}
