package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;

/**
 *
 * @author jpaoletti
 */
public interface Searcher {

    public String visualization();

    public Criterion build(Field field, Map<String, Object> parameters);
}
