package jpaoletti.jpm2.web.converter;

import java.util.Date;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 *
 * @author jpaoletti
 */
public class PlainShowDateTime extends WebToString {

    private String format = "yyyy-MM-dd";

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Date value = (Date) object;
        if (value != null) {
            return process(DateFormatUtils.format(value, format));
        } else {
            return "-";
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
