package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class ShowFileConverter extends Converter {

    private String contentType = "";
    private String prefix;
    private String sufix;
    private String measure = "k";
    private boolean downloadable = false;
    private String filenameField = null;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final byte[] value = (byte[]) getValue(object, field);
        String page = "@page:show-file-converter.jsp"
                + "?contentType=" + getContentType()
                + "&instanceId=" + instanceId
                + "&downloadable=" + isDownloadable();
        if (getFilenameField() != null) {
            final Field f = contextualEntity.getEntity().getFieldById(getFilenameField(), contextualEntity.getContext());
            page = page + "prefix=" + JPMUtils.get(object, f.getProperty()) + "sufix=";
        } else {
            page = page
                    + "&prefix=" + (getPrefix() == null ? contextualEntity.getEntity().getId() : getPrefix())
                    + "&sufix=" + (getSufix() == null ? ".dat" : getSufix());
        }
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
            return page + "&len=" + len;
        } else {
            return page;
        }
    }

    public String getSufix() {
        return sufix;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilenameField() {
        return filenameField;
    }

    public void setFilenameField(String filenameField) {
        this.filenameField = filenameField;
    }
}
