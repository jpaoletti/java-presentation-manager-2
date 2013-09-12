package jpaoletti.jpm2.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class JPMController extends BaseController {

    @RequestMapping(value = "/jpm", method = RequestMethod.GET)
    public ModelAndView jpmStatus() {
        final ModelAndView mav = new ModelAndView("jpm-status");
        mav.addObject("jpm", getJpm());
        return mav;
    }
}
