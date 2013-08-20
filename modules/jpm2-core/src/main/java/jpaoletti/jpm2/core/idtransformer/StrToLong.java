package jpaoletti.jpm2.core.idtransformer;

import java.io.Serializable;

/**
 *
 * @author jpaoletti
 */
public class StrToLong implements IdTransformer {

    @Override
    public Serializable transform(Serializable input) {
        return Long.parseLong((String) input);
    }
}
