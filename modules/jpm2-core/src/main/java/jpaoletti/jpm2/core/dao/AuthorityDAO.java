package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.Authority;

/**
 *
 * @author jpaoletti
 */
public class AuthorityDAO extends GenericDAO<Authority, String> {

    @Override
    public String getId(Object object) {
        return ((Authority) object).getId();
    }
}
