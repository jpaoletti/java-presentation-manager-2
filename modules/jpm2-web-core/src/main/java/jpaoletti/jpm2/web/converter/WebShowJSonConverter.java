package jpaoletti.jpm2.web.converter;

import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
public class WebShowJSonConverter extends WebToString {

    @Autowired
    private HttpServletRequest request;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final JSONObject value = (JSONObject) ((object == null) ? null : object);
        if (value == null || value.isEmpty()) {
            return "";
        } else {
            request.setAttribute("wsjsonc_values", value.toMap());
            return "@page:show-map-converter.jsp";
        }
    }

}
