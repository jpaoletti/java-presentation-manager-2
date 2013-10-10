package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class ShowFileConverter extends Converter {

    private String measure = "k";

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final byte[] value = (byte[]) getValue(object, field);
        if (value != null && value.length > 0) {
            String len;
            switch (getMeasure().charAt(0)) {
                case 'b':
                    len = value.length + " bytes";
                    break;
                case 'm':
                    len = (value.length / 1024 / 1024) + " Mb";
                    break;
                case 'k':
                    ;
                default:
                    len = (value.length / 1024) + "Kb";
            }
            return "@page:show-file-converter.jsp?len=" + len;
        } else {
            return "@page:show-file-converter.jsp?";
        }
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}
