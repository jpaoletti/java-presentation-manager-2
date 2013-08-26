package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import jpaoletti.jpm2.core.security.Authority;

/**
 *
 * @author jpaoletti
 */
public class AuthorityDAO extends GenericDAO<Authority, String> {

    @Override
    public Serializable getId(Object object) {
        return ((Authority) object).getId();
    }
}
