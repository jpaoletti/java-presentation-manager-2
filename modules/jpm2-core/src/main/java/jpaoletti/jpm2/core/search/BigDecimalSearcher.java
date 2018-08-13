package jpaoletti.jpm2.core.search;

import java.math.BigDecimal;
import java.util.Map;
import jpaoletti.jpm2.core.model.Field;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class BigDecimalSearcher extends IntegerSearcher {

    private String format = "#0.00";
    private Character decimalSeparator = '.';
    private Character groupingSeparator = ',';
    private String min = "0.00";
    private String max = "999999999.99";

    @Override
    public String visualization(Field field) {
        return "@page:bigdecimal-searcher.jsp?options=" + getOptions();
    }

    @Override
    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        return new BigDecimal(parameters.get("value")[0]);
    }

    public String getOptions() {
        return String.format("{aSep: '%s', aDec: '%s', vMin: '%s', vMax: '%s'}", getGroupingSeparator(), getDecimalSeparator(), getMin(), getMax());
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Character getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(Character decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public Character getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(Character groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

}
