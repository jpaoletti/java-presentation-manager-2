package jpaoletti.jpm2.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.security.SecurityService;
import jpaoletti.jpm2.core.security.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Reset a user password and generates a new one.
 *
 * @author jpaoletti
 */
@Controller
public class SecurityController extends BaseController {

    private SecurityService securityService;

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        final Operation operation = entity.getOperation("resetPassword");
        getContext().setEntity(entity);
        getContext().setOperation(operation);
        getContext().setEntityInstance(newEntityInstance(instanceId, entity));
        final User user = getSecurityService().resetPassword(entity, operation, instanceId);
        getContext().setObject(user);
        return newMav();
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
