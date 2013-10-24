package jpaoletti.jpm2.core.test;

import jpaoletti.jpm2.core.dao.GenericDAO;

/**
 * DAO for weak test class.
 *
 * @author jpaoletti
 */
public class JPMTestWeakDAOImpl extends GenericDAO<JPMTestWeak, Long> {

    @Override
    public Long getId(Object object) {
        return ((JPMTestWeak) object).getId();
    }
}
