package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.Authority;

/**
 *
 * @author jpaoletti
 */
public class AuthorityJpaDAO extends JPADAO<Authority, String> {

    private final ThreadLocal<String> lastFetchedId = new ThreadLocal<>();

    @Override
    public String getId(Object object) {
        return ((Authority) object).getId();
    }

    @Override
    public Authority get(String id) {
        lastFetchedId.set(id);
        return super.get(id);
    }

    @Override
    public void update(final Object object) {
        Authority authority = (Authority) object;
        String oldId = lastFetchedId.get();
        String newId = authority.getId();
        if (oldId != null && !newId.equals(oldId)) {
            authority.setId(oldId);
            getSession().evict(authority);
            authority.setId(newId);
            getSession().createNativeQuery("UPDATE authorities SET authority = :newId WHERE authority = :oldId")
                    .setParameter("newId", newId)
                    .setParameter("oldId", oldId)
                    .executeUpdate();
            getSession().createNativeQuery("UPDATE group_authorities SET authority = :newId WHERE authority = :oldId")
                    .setParameter("newId", newId)
                    .setParameter("oldId", oldId)
                    .executeUpdate();
        } else {
            super.update(object);
        }
    }
}
