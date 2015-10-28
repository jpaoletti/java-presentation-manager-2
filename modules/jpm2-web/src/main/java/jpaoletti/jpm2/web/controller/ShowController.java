package jpaoletti.jpm2.web.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import static jpaoletti.jpm2.core.converter.ToStringConverter.DISPLAY_PATTERN;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.ObjectConverterData;
import jpaoletti.jpm2.web.ObjectConverterData.ObjectConverterDataItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public final class ShowController extends BaseController {

    public static final String OP_SHOW = "show";

    @RequestMapping(value = "/jpm/{entity}/{instanceId}.json", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ObjectConverterData.ObjectConverterDataItem listObject(
            @PathVariable Entity entity,
            @PathVariable String instanceId,
            @RequestParam(required = false) String textField) throws PMException {
        final IdentifiedObject iobject = getService().get(entity, getContext().getEntityContext(), instanceId);
        final Object object = iobject.getObject();
        if (object == null) {
            return new ObjectConverterDataItem("", "");
        }
        if (textField != null) {
            if (!textField.contains("{")) {
                final Field field = entity.getFieldById(textField, getContext().getEntityContext());
                return new ObjectConverterDataItem(entity.getDao(getContext().getEntityContext()).getId(object).toString(), JPMUtils.get(object, field.getProperty()).toString());
            } else {
                final Matcher matcher = DISPLAY_PATTERN.matcher(textField);
                String finalValue = textField;
                while (matcher.find()) {
                    final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                    final Field field2 = entity.getFieldById(_display_field.replaceAll("\\!", ""), getContext().getEntityContext());
                    finalValue = finalValue.replace("{" + _display_field + "}", String.valueOf(JPMUtils.get(object, field2.getProperty())));
                }
                return new ObjectConverterDataItem(entity.getDao(getContext().getEntityContext()).getId(object).toString(), finalValue);
            }
        } else {
            return new ObjectConverterDataItem(entity.getDao(getContext().getEntityContext()).getId(object).toString(), object.toString());
        }
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_SHOW + "}.json", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public Map<String, Object> showJSON(@PathVariable String instanceId, @RequestParam(required = false) String fields) throws PMException {
        final Object object = getService().get(getContext().getEntity(), getContext().getEntityContext(), getContext().getOperation(), instanceId).getObject();
        final Map<String, Object> values = new LinkedHashMap<>();
        final String[] fs = fields.split("[,]");
        for (String fid : fs) {
            final Field field = getContext().getEntity().getFieldById(fid, getContext().getEntityContext());
            final Converter converter = field.getConverter(getContext().getEntityInstance(), getContext().getOperation());
            if (converter != null) {
                try {
                    values.put(field.getTitle(getContext().getEntity()),
                            converter.visualize(getContext().getContextualEntity(), field, object, instanceId));
                } catch (IgnoreConvertionException ex) {
                }
            }
        }
        return values;
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_SHOW + "}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable String instanceId) throws PMException {
        initItemControllerOperation(instanceId);
        return new ModelAndView("jpm-" + OP_SHOW);
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/download/{fieldId}")
    public void downloadFileConverter(HttpServletResponse response,
            @PathVariable String instanceId,
            @PathVariable String fieldId,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) String prefix,
            @RequestParam(required = false) String sufix
    ) throws IOException, PMException {
        getContext().setOperation(getContext().getEntity().getOperation(OP_SHOW, getContext().getContext()));
        final IdentifiedObject iobject = initItemControllerOperation(instanceId);
        response.setContentType(contentType);
        response.addHeader("Content-Disposition", "attachment;filename=" + prefix + "." + iobject.getId() + sufix);
        response.getOutputStream().write((byte[]) JPMUtils.get(iobject.getObject(), getContext().getEntity().getFieldById(fieldId, getContext().getEntityContext()).getProperty()));
    }
}
