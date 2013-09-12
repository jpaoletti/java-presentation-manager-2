package jpaoletti.jpm2.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class LoginController extends BaseController {

    @RequestMapping(value = "/login")
    @Transactional
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        final ModelAndView result = new ModelAndView("login");
        if (error != null) {
            result.addObject("error", error);
            getJpm().audit("Login error: " + error);
        }
        return result;
    }
}
