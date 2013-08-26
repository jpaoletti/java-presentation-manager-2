package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import jpaoletti.jpm2.core.security.User;

/**
 *
 * @author jpaoletti
 */
public class UserDAO extends GenericDAO<User, String> {

    @Override
    public Serializable getId(Object object) {
        return ((User) object).getUsername();
    }
}
