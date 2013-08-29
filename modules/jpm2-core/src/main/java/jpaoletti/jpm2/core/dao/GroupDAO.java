package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.security.Group;

/**
 *
 * @author jpaoletti
 */
public class GroupDAO extends GenericDAO<Group, Long> {

    @Override
    public Long getId(Object object) {
        return ((Group) object).getId();
    }
}
