package jpaoletti.jpm2.core.idtransformer;

/**
 *
 * @author jpaoletti
 */
public class StrToInt implements IdTransformer<Integer> {

    @Override
    public Integer transform(String input) {
        return Integer.parseInt(input);
    }
}
