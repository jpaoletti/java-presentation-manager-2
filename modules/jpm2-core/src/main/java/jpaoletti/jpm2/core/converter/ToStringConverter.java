package jpaoletti.jpm2.core.converter;

import java.util.Date;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class ToStringConverter extends Converter {

    @Override
    public Object visualize(Field field, Object object) throws ConverterException {
        final Object value = getValue(object, field);
        return process(value);
    }

    protected String process(final Object value) {
        Integer pad = 0;
        String padc = getConfig("pad-count", "0");
        try {
            pad = Integer.parseInt(padc);
        } catch (Exception e) {
        }
        char padch = getConfig("pad-char", " ").charAt(0);
        String padd = getConfig("pad-direction", "left");

        String prefix = getConfig("prefix");
        String suffix = getConfig("suffix");
        String res = value != null ? value.toString() : "";
        if (padd.compareToIgnoreCase("left") == 0) {
            res = JPMUtils.padleft(res, pad, padch);
        } else {
            res = JPMUtils.padright(res, pad, padch);
        }
        if (prefix != null) {
            res = prefix + res;
        }
        if (suffix != null) {
            res = res + suffix;
        }
        return res;
    }
}
