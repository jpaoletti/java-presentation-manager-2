package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
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

    public static final String OP_ITEM_AUDIT = "itemAudit";
    public static final String OP_GENERAL_AUDIT = "generalAudit";

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/" + OP_ITEM_AUDIT, method = RequestMethod.GET)
    public ModelAndView getItemAudit(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        final Operation operation = entity.getOperation(OP_ITEM_AUDIT);
        final IdentifiedObject iobject = getJpm().getService().get(entity, operation, instanceId);
        final ModelAndView mav = new ModelAndView("jpm-" + OP_ITEM_AUDIT);
        getContext().set(entity, operation);
        getContext().setEntityInstance(new EntityInstance(iobject, entity, operation));
        mav.addObject("audits", getJpm().getAuditService().getItemRecords(entity, instanceId));
        return mav;
    }

    @RequestMapping(value = "/jpm/{entity}/" + OP_GENERAL_AUDIT, method = RequestMethod.GET)
    public ModelAndView getGeneralAudit(@PathVariable Entity entity) throws PMException {
        final Operation operation = entity.getOperation(OP_GENERAL_AUDIT);
        final ModelAndView mav = new ModelAndView("jpm-" + OP_GENERAL_AUDIT);
        getContext().set(entity, operation);
        mav.addObject("audits", getJpm().getAuditService().getGeneralRecords(entity));
        return mav;
    }
}
