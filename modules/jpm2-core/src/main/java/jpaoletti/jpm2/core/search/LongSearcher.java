package jpaoletti.jpm2.core.search;

import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author jpaoletti
 */
public class LongSearcher extends IntegerSearcher {

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
