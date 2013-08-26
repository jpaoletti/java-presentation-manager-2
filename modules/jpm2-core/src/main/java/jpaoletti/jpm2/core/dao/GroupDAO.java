package jpaoletti.jpm2.core.dao;

import java.io.Serializable;
import jpaoletti.jpm2.core.security.Group;

/**
 *
 * @author jpaoletti
 */
public class GroupDAO extends GenericDAO<Group, Long> {

    @Override
    public Serializable getId(Object object) {
        return ((Group) object).getId();
    }
}
