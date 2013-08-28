package jpaoletti.jpm2.core.search;

import java.util.Map;

/**
 *
 * @author jpaoletti
 */
public class LongSearcher extends IntegerSearcher {

    @Override
    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        return Long.parseLong(parameters.get("value")[0]);
    }
}
