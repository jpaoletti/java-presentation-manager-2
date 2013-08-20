package jpaoletti.jpm2.core.idtransformer;

import java.io.Serializable;

/**
 * Receives a generic representation of the id (normally a String) and we need
 * to transform it to its final serializable form.
 *
 * @author jpaoletti
 */
public interface IdTransformer {

    public Serializable transform(Serializable input);
}