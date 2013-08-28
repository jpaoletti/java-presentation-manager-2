package jpaoletti.jpm2.core.test;

import java.io.Serializable;
import jpaoletti.jpm2.core.dao.GenericDAO;
import org.springframework.stereotype.Repository;

/**
 * DAO for test class.
 *
 * @author jpaoletti
 */
@Repository
public class JPMTestDAO extends GenericDAO<JPMTest, Long> {

    @Override
    public Serializable getId(Object object) {
        return ((JPMTest) object).getId();
    }
}
