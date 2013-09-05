package jpaoletti.jpm2.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import jpaoletti.jpm2.core.service.JPMService;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.ObjectConverterData;
import jpaoletti.jpm2.web.ObjectConverterData.ObjectConverterDataItem;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JPMService service;

    @RequestMapping(value = "/jpm/{entity}/{instanceId}.json", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ObjectConverterData.ObjectConverterDataItem listObject(
            @PathVariable Entity entity, @PathVariable String instanceId, @RequestParam String textField,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) throws PMException {
        final Field field = entity.getFieldById(textField);
        final Object object = getService().get(entity, instanceId);
        return new ObjectConverterDataItem(entity.getDao().getId(object).toString(), JPMUtils.get(object, field.getProperty()).toString());
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show.json", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public Map<String, Object> showJSON(@PathVariable Entity entity, @PathVariable String instanceId, @RequestParam(required = false) String fields) throws PMException {
        getContext().setOperation(entity.getOperation("show"));
        final Object object = getService().get(entity, getContext().getOperation(), instanceId);
        getContext().setObject(object);
        final Map<String, Object> values = new LinkedHashMap<>();
        final String[] fs = fields.split("[,]");
        for (String fid : fs) {
            final Field field = entity.getFieldById(fid);
            final Converter converter = field.getConverter(getContext().getOperation());
            if (converter != null) {
                try {
                    values.put(field.getTitle(entity), converter.visualize(field, object, instanceId));
                } catch (IgnoreConvertionException ex) {
                }
            }
        }
        return values;
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        prepareItemOperation(entity, instanceId, "show");
        return new ModelAndView("jpm-show");
    }

    public JPMService getService() {
        return service;
    }

    public void setService(JPMService service) {
        this.service = service;
    }
}
