package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
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

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_ITEM_AUDIT + "}", method = RequestMethod.GET)
    public ModelAndView getItemAudit(@PathVariable String instanceId) throws PMException {
        final IdentifiedObject iobject = getJpm().getService().get(getContext().getEntity(), getContext().getOperation(), instanceId);
        final ModelAndView mav = new ModelAndView("jpm-" + OP_ITEM_AUDIT);
        getContext().setEntityInstance(new EntityInstance(iobject, getContext().getEntity(), getContext().getOperation()));
        mav.addObject("audits", getJpm().getAuditService().getItemRecords(getContext().getEntity(), instanceId));
        return mav;
    }

    @RequestMapping(value = "/jpm/{entity}/{operationId:" + OP_GENERAL_AUDIT + "}", method = RequestMethod.GET)
    public ModelAndView getGeneralAudit() throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-" + OP_GENERAL_AUDIT);
        mav.addObject("audits", getJpm().getAuditService().getGeneralRecords(getContext().getEntity()));
        return mav;
    }

    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/{operationId:" + OP_GENERAL_AUDIT + "}", method = RequestMethod.GET)
    public ModelAndView getGeneralAudit(@PathVariable Entity owner, @PathVariable String ownerId) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-" + OP_GENERAL_AUDIT);
        mav.addObject("audits", getJpm().getAuditService().getGeneralRecords(getContext().getEntity()));
        mav.addObject("owner", owner);
        mav.addObject("ownerId", ownerId);
        return mav;
    }
}
