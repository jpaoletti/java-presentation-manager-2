package jpaoletti.jpm2.core.idtransformer;

/**
 *
 * @author jpaoletti
 */
public class StrToLong implements IdTransformer<Long> {

    @Override
    public Long transform(String input) {
        return Long.parseLong(input);
    }
}
