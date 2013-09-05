package jpaoletti.jpm2.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class AuditController extends BaseController {

    @Autowired
    private AuditService service;

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/itemAudit", method = RequestMethod.GET)
    public ModelAndView getItemAudit(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        prepareItemOperation(entity, instanceId, "itemAudit");
        final ModelAndView mav = newMav();
        mav.addObject("audits", getService().getItemRecords(instanceId));
        return mav;
    }

    @RequestMapping(value = "/jpm/{entity}/generalAudit", method = RequestMethod.GET)
    public ModelAndView getGeneralAudit(@PathVariable Entity entity) throws PMException {
        prepareItemOperation(entity, null, "generalAudit");
        final ModelAndView mav = newMav();
        mav.addObject("audits", getService().getGeneralRecords());
        return mav;
    }

    public AuditService getService() {
        return service;
    }

    public void setService(AuditService service) {
        this.service = service;
    }
}
