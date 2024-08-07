package jpaoletti.jpm2.web.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.WithAttachment;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class ShowAttachmentConverter extends Converter {

    private String measure = "k";
    private boolean showName = true;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final WithAttachment wa = (WithAttachment) object;
        byte[] value = null;
        if (wa != null && wa.isExternalFile()) {
            final File file = new File(wa.getInternalFileName());
            final FileInputStream is;
            try {
                is = new FileInputStream(file);
                value = IOUtils.toByteArray(is);
                IOUtils.closeQuietly(is);
            } catch (FileNotFoundException ex) {
                value = null;
            } catch (IOException ex) {
                JPMUtils.getLogger().error("Error in ShowAttachmentConverter.visualize", ex);
            }
        } else {
            value = (byte[]) getValue(object, field);
        }
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
            return "@page:show-attachment-converter.jsp"
                    + "?len=" + len
                    + "&downloadable=" + note.isDownloadable()
                    + "&contentType=" + note.getContentType()
                    + "&attachmentName=" + (showName ? note.getAttachmentName() : "")
                    + "&noteId=" + instanceId
                    + "&entity=" + contextualEntity.getEntity().getId();
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

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }
}
