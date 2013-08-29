package jpaoletti.jpm2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        final ModelAndView result = new ModelAndView("login");
        result.addObject("error", error);
        return result;
    }
}
