package jpaoletti.jpm2.core.search.jpa;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * JPA Criteria API implementation of long searcher.
 * Extends IntegerJPASearcher and overrides getValue to parse Long instead of Integer.
 *
 * @author jpaoletti
 */
public class LongJPASearcher extends IntegerJPASearcher {

    @Override
    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        final String v = parameters.get("value")[0];
        if (StringUtils.isEmpty(v)) {
            return null;
        } else {
            return Long.parseLong(v);
        }
    }
}
