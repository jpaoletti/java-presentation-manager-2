package jpaoletti.jpm2.core.idtransformer;

import java.io.Serializable;

/**
 *
 * @author jpaoletti
 */
public class Str implements IdTransformer {

    @Override
    public Serializable transform(Serializable input) {
        return (String) input;
    }
}
