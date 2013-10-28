package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.service.SecurityService;
import jpaoletti.jpm2.core.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Reset a user password and generates a new one.
 *
 * @author jpaoletti
 */
@Controller
public class SecurityController extends BaseController {

    public static final String OP_RESET_PASSWORD = "resetPassword";
    public static final String OP_PROFILE = "profile";
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

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/" + OP_PROFILE)
    public ModelAndView profile(
            @PathVariable Entity entity,
            @PathVariable String instanceId,
            @RequestParam(required = false) String current,
            @RequestParam(required = false) String newpass) throws PMException {
        final Operation operation = entity.getOperation(OP_PROFILE);
        getContext().set(entity, operation);
        final IdentifiedObject iobject = getService().get(entity, operation, instanceId);
        final UserDetails user = (UserDetails) iobject.getObject();
        if (!getUserDetails().getUsername().equals(user.getUsername())) {
            throw new NotAuthorizedException();
        }
        getContext().setEntityInstance(new EntityInstance(iobject, entity, operation));
        if (current != null) {
            getSecurityService().changePassword(entity, operation, instanceId, current, newpass);
        }
        return new ModelAndView("jpm-" + OP_PROFILE);
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
