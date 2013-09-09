package jpaoletti.jpm2.core.test;

import jpaoletti.jpm2.core.dao.GenericDAO;

/**
 * DAO for test class.
 *
 * @author jpaoletti
 */
public class JPMTestDAO extends GenericDAO<JPMTest, Long> {

    @Override
    public Long getId(Object object) {
        return ((JPMTest) object).getId();
    }
}
