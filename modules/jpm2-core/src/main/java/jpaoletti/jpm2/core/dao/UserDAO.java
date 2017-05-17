package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.JpmUser;

/**
 *
 * @author jpaoletti
 */
public class UserDAO extends GenericDAO<JpmUser, String> {

    @Override
    public String getId(Object object) {
        return ((JpmUser) object).getUsername();
    }
}
