package jpaoletti.jpm2.web.converter;

import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * HTML editor using a visual rich text widget instead of a raw code editor.
 *
 * @author jpaoletti
 */
public class WebEditRichHtmlConverter extends EditBigString {

    @Autowired
    private HttpServletRequest request;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final String p = (String) getValue(object, field.getProperty());
        final String value = (p != null) ? p : "";
        getRequest().setAttribute("htmlRichConverterValue", value);
        return "@page:html-rich-converter.jsp";
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

}
