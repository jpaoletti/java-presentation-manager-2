package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class ShowImageConverter extends Converter {

    private String contentType = "";
    private String prefix;
    private String sufix;
    /**
     * Allows anonymous access to the image url (/static/img/...). Use only for
     * images that must be reachable without a session (e.g. mails).
     */
    private boolean publicAccess = false;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        String page = "@page:show-image-converter.jsp"
                + "?contentType=" + getContentType()
                + "&instanceId=" + instanceId
                + "&entityId=" + contextualEntity.toString()
                + "&fieldId=" + field.getId();
        page = page
                + "&prefix=" + (getPrefix() == null ? contextualEntity.getEntity().getId() : getPrefix())
                + "&sufix=" + (getSufix() == null ? ".dat" : getSufix());
        return page;
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public String getSufix() {
        return sufix;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
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
}
