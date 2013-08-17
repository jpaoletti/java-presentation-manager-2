package jpaoletti.jpm2.core.test.dao;

import java.io.Serializable;
import jpaoletti.jpm2.core.dao.GenericDAO;
import jpaoletti.jpm2.core.test.model.JPMTest;

/**
 * DAO for test class.
 *
 * @author jpaoletti
 */
public class JPMTestDAO extends GenericDAO<JPMTest, Long> {

    @Override
    public Serializable getId(Object object) {
        return ((JPMTest) object).getId();
    }
}
