package jpaoletti.jpm2.core.test;

import jpaoletti.jpm2.core.dao.GenericDAO;

/**
 * DAO for test class.
 *
 * @author jpaoletti
 */
public class JPMTestDAOImpl extends GenericDAO<JPMTest, Long> implements JPMTestDAO {

    @Override
    public Long getId(Object object) {
        return ((JPMTest) object).getId();
    }
}
