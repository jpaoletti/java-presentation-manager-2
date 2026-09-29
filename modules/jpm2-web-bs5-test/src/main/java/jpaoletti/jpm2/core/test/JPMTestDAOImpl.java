package jpaoletti.jpm2.core.test;

import jpaoletti.jpm2.core.dao.HibernateCriteriaDAO;

/**
 * DAO for test class.
 *
 * @author jpaoletti
 */
public class JPMTestDAOImpl extends HibernateCriteriaDAO<JPMTest, Long> implements JPMTestDAO {

    @Override
    public Long getId(Object object) {
        return ((JPMTest) object).getId();
    }
}
