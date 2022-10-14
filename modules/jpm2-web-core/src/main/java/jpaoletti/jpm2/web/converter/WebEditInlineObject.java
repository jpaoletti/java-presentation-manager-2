package jpaoletti.jpm2.web.converter;

import java.util.List;
import static jpaoletti.jpm2.core.converter.Converter.getValue;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.web.ObjectConverterData;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.json.JSONObject;

/**
 *
 * @author jpaoletti
 */
public class WebEditInlineObject extends WebEditObject {

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = (object == null) ? null : getValue(object, field);
        final StringBuilder sb = new StringBuilder("@page:webEditInlineObject-converter.jsp?instanceId=" + instanceId);
        final List list = getEntity().getDao().list(null);
        final JSONObject options = new JSONObject();
        for (Object o : list) {
            final String id = getEntity().getDao().getId(o).toString();
            final ObjectConverterData.ObjectConverterDataItem data = ObjectConverterData.buildDataObject(getTextField(), getEntity(), null, id, o);
            options.put(id, data.getText());
        }
        sb.append("&options=").append(options.toString());
        if (value != null) {
            final ObjectConverterData.ObjectConverterDataItem data = ObjectConverterData.buildDataObject(getTextField(), getEntity(), null, getEntity().getDao().getId(value).toString(), value);
            sb.append("&finalValue=").append(Util.URLEncode(data.getText(), "UTF-8"));
        }
        return sb.toString();
    }
}
