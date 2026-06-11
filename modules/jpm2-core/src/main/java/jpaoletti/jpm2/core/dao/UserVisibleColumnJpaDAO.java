package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.model.UserVisibleColumn;

/**
 *
 * @author jpaoletti
 */
public class UserVisibleColumnJpaDAO extends JPADAO<UserVisibleColumn, Long> {

    @Override
    public Long getId(Object object) {
        return ((UserVisibleColumn) object).getId();
    }
}
