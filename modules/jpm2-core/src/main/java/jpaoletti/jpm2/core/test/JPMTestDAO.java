package jpaoletti.jpm2.core.test;

import java.io.Serializable;
import jpaoletti.jpm2.core.dao.GenericDAO;

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
