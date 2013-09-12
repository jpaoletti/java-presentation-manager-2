package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.dao.GenericDAO;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class IndexController extends BaseController {

    @Autowired
    private WebApplicationContext ctx;

    @RequestMapping(value = {"", "/", "/index", "/home"})
    public String index() {
        setCurrentHome("index");
        return "index";
    }

    @RequestMapping(value = "/security")
    public ModelAndView security() {
        final ModelAndView res = new ModelAndView("security");
        if (getCtx().containsBean("jpm-dao-auth")) {
            final GenericDAO dao = (GenericDAO) getCtx().getBean("jpm-dao-auth");
            res.addObject("authorities", dao.list());
        }
        if (getCtx().containsBean("jpm-dao-user")) {
            final GenericDAO dao = (GenericDAO) getCtx().getBean("jpm-dao-user");
            res.addObject("enabledUsersCount", dao.count(Restrictions.eq("enabled", true)));
            res.addObject("disabledUsersCount", dao.count(Restrictions.eq("enabled", false)));
        }
        if (getCtx().containsBean("jpm-dao-group")) {
            final GenericDAO dao = (GenericDAO) getCtx().getBean("jpm-dao-group");
            res.addObject("groupCount", dao.count());
        }
        setCurrentHome("security");
        return res;
    }

    public WebApplicationContext getCtx() {
        return ctx;
    }

    public void setCtx(WebApplicationContext ctx) {
        this.ctx = ctx;
    }
}
