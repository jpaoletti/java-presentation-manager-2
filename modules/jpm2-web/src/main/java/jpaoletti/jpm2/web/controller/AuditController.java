package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.EntityPath;
import jpaoletti.jpm2.core.model.IdentifiedObject;
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

    @RequestMapping(value = "/jpm/{entityPath}/{instanceId}/" + OP_ITEM_AUDIT, method = RequestMethod.GET)
    public ModelAndView getItemAudit(
            @PathVariable EntityPath entityPath,
            @PathVariable String instanceId) throws PMException {
        getContext().set(entityPath, OP_ITEM_AUDIT);
        final IdentifiedObject iobject = getJpm().getService().get(entityPath.getEntity(), getContext().getOperation(), instanceId);
        final ModelAndView mav = new ModelAndView("jpm-" + OP_ITEM_AUDIT);
        getContext().setEntityInstance(new EntityInstance(iobject, entityPath.getEntity(), getContext().getOperation(), entityPath.getOwner()));
        mav.addObject("audits", getJpm().getAuditService().getItemRecords(entityPath.getEntity(), instanceId));
        return mav;
    }

    @RequestMapping(value = "/jpm/{entityPath}/" + OP_GENERAL_AUDIT, method = RequestMethod.GET)
    public ModelAndView getGeneralAudit(@PathVariable EntityPath entityPath) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-" + OP_GENERAL_AUDIT);
        getContext().set(entityPath, OP_GENERAL_AUDIT);
        mav.addObject("audits", getJpm().getAuditService().getGeneralRecords(entityPath.getEntity()));
        return mav;
    }

    @RequestMapping(value = "/jpm/{ownerPath}/{ownerId}/{entityPath}/" + OP_GENERAL_AUDIT, method = RequestMethod.GET)
    public ModelAndView getGeneralAudit(
            @PathVariable EntityPath owner,
            @PathVariable String ownerId,
            @PathVariable EntityPath entityPath) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-" + OP_GENERAL_AUDIT);
        getContext().set(entityPath, OP_GENERAL_AUDIT);
        mav.addObject("audits", getJpm().getAuditService().getGeneralRecords(entityPath.getEntity()));
        mav.addObject("owner", owner);
        mav.addObject("ownerId", ownerId);
        return mav;
    }
}
