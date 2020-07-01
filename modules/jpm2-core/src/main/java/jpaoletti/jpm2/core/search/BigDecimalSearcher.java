package jpaoletti.jpm2.core.search;

import java.math.BigDecimal;
import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.apache.commons.lang.StringUtils;
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
    public String visualization(Entity entity, Field field) {
        return "@page:bigdecimal-searcher.jsp?options=" + getOptions();
    }

    @Override
    protected Object getValue(Map<String, String[]> parameters) throws NumberFormatException {
        final String v = parameters.get("value")[0];
        if (StringUtils.isEmpty(v)) {
            return null;
        } else {
            return new BigDecimal(v);
        }

    }

    public String getOptions() {
        return String.format("{digitGroupSeparator: '%s', decimalCharacter: '%s', minimumValue: '%s', maximumValue: '%s'}", getGroupingSeparator(), getDecimalSeparator(), getMin(), getMax());
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
