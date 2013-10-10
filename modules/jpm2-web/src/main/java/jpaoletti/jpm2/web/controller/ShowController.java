package jpaoletti.jpm2.web.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
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
        final IdentifiedObject iobject = getService().get(entity, instanceId);
        final Object object = iobject.getObject();
        if (textField != null) {
            if (!textField.contains("{")) {
                final Field field = entity.getFieldById(textField);
                return new ObjectConverterDataItem(entity.getDao().getId(object).toString(), JPMUtils.get(object, field.getProperty()).toString());
            } else {
                final Matcher matcher = ListController.DISPLAY_PATTERN.matcher(textField);
                String finalValue = textField;
                while (matcher.find()) {
                    final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                    final Field field2 = entity.getFieldById(_display_field);
                    finalValue = finalValue.replace("{" + _display_field + "}", String.valueOf(JPMUtils.get(object, field2.getProperty())));
                }
                return new ObjectConverterDataItem(entity.getDao().getId(object).toString(), finalValue);
            }
        } else {
            return new ObjectConverterDataItem(entity.getDao().getId(object).toString(), object.toString());
        }
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show.json", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public Map<String, Object> showJSON(@PathVariable Entity entity, @PathVariable String instanceId, @RequestParam(required = false) String fields) throws PMException {
        final Operation operation = entity.getOperation(OP_SHOW);
        getContext().set(entity, operation);
        final Object object = getService().get(entity, operation, instanceId).getObject();
        final Map<String, Object> values = new LinkedHashMap<>();
        final String[] fs = fields.split("[,]");
        for (String fid : fs) {
            final Field field = entity.getFieldById(fid);
            final Converter converter = field.getConverter(operation);
            if (converter != null) {
                try {
                    values.put(field.getTitle(entity), converter.visualize(field, object, instanceId));
                } catch (IgnoreConvertionException ex) {
                }
            }
        }
        return values;
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/" + OP_SHOW, method = RequestMethod.GET)
    public ModelAndView show(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        final Operation operation = entity.getOperation(OP_SHOW);
        getContext().set(entity, operation);
        if (instanceId != null) {
            final IdentifiedObject iobject = getService().get(entity, operation, instanceId);
            getContext().setEntityInstance(new EntityInstance(iobject, entity, operation));
        }
        return new ModelAndView("jpm-" + OP_SHOW);
    }
}
