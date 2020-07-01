package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.WithAttachment;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class ShowAttachmentConverter extends Converter {

    private String measure = "k";

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final byte[] value = (byte[]) getValue(object, field);
        if (value != null && value.length > 0) {
            final WithAttachment note = (WithAttachment) object;
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
            return "@page:show-attachment-converter.jsp?len=" + len + "&contentType=" + note.getContentType() + "&noteId=" + instanceId + "&entity=" + contextualEntity.getEntity().getId();
        } else {
            return "@page:show-attachment-converter.jsp?";
        }
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}
