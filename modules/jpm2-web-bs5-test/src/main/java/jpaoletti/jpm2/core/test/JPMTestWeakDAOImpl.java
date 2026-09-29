package jpaoletti.jpm2.core.test;

import jpaoletti.jpm2.core.dao.HibernateCriteriaDAO;

/**
 * DAO for weak test class.
 *
 * @author jpaoletti
 */
public class JPMTestWeakDAOImpl extends HibernateCriteriaDAO<JPMTestWeak, Long> {

    @Override
    public Long getId(Object object) {
        return ((JPMTestWeak) object).getId();
    }
}
