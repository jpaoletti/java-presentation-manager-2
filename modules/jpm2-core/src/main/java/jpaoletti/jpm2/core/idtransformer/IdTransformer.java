package jpaoletti.jpm2.core.idtransformer;

/**
 * Receives a generic representation of the id in a String form and translate it
 * to a final id form.
 *
 * @author jpaoletti
 */
public interface IdTransformer<T> {

    public T transform(String input);
}