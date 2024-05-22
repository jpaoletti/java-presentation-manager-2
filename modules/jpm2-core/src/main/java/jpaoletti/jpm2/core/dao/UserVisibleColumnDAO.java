package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.model.UserVisibleColumn;

/**
 *
 * @author jpaoletti
 */
public class UserVisibleColumnDAO extends HibernateCriteriaDAO<UserVisibleColumn, String> {

    @Override
    public String getId(Object object) {
        return Long.toString(((UserVisibleColumn) object).getId());
    }
}
