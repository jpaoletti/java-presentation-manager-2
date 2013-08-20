package jpaoletti.jpm2.core.idtransformer;

import java.io.Serializable;

/**
 *
 * @author jpaoletti
 */
public class StrToInt implements IdTransformer {

    @Override
    public Serializable transform(Serializable input) {
        return Integer.parseInt((String) input);
    }
}
