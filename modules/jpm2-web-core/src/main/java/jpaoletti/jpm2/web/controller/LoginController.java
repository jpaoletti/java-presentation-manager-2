package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class LoginController extends BaseController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    @Qualifier("authenticationManager")
    protected AuthenticationManager authenticationManager;

    @GetMapping(value = "/login")
    @Transactional
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        final ModelAndView result = new ModelAndView(getSecurityService().getLoginPage());
        if (error != null) {
            result.addObject("error", error);
            getJpm().audit("Login error: " + error);
        }
        return result;
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

}
