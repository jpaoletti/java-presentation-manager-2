package jpaoletti.jpm2.core.idtransformer;

/**
 *
 * @author jpaoletti
 */
public class Str implements IdTransformer<String> {

    @Override
    public String transform(String input) {
        return input;
    }
}
