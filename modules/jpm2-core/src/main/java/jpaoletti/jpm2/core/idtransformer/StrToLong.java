package jpaoletti.jpm2.core.idtransformer;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jpaoletti
 */
public class StrToLong implements IdTransformer<Long> {

    @Override
    public Long transform(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        return Long.parseLong(input);
    }
}
