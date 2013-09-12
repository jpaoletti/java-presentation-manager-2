package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.service.SecurityService;
import jpaoletti.jpm2.core.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Reset a user password and generates a new one.
 *
 * @author jpaoletti
 */
@Controller
public class SecurityController extends BaseController {

    public static final String OP_RESET_PASSWORD = "resetPassword";
    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/" + OP_RESET_PASSWORD)
    public ModelAndView resetPassword(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        final Operation operation = entity.getOperation(OP_RESET_PASSWORD);
        getContext().set(entity, operation);
        final User user = getSecurityService().resetPassword(entity, operation, instanceId);
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(instanceId, user), entity, operation));
        return new ModelAndView("jpm-" + OP_RESET_PASSWORD);
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
