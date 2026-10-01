package jpaoletti.jpm2.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.HibernateCriteriaDAO;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.WithAttachment;
import org.apache.commons.io.IOUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
//@Controller // Uncomment this  line for a stand-alone behaviour
public class IndexController extends BaseController {

    @Autowired
    private WebApplicationContext ctx;

    @GetMapping(value = {"", "/", "/index", "/home"})
    public ModelAndView index() throws PMException {
        setCurrentHome("index");
        return new ModelAndView("index");
    }

    @GetMapping(value = "/security")
    public ModelAndView security() {
        final ModelAndView res = new ModelAndView("security");
        if (getCtx().containsBean("jpm-dao-auth")) {
            final HibernateCriteriaDAO dao = (HibernateCriteriaDAO) getCtx().getBean("jpm-dao-auth");
            res.addObject("authorities", dao.list(new DAOListConfiguration()));
        }
        if (getCtx().containsBean("jpm-dao-user")) {
            final HibernateCriteriaDAO dao = (HibernateCriteriaDAO) getCtx().getBean("jpm-dao-user");
            res.addObject("enabledUsersCount", dao.count(new DAOListConfiguration(Restrictions.eq("enabled", true))));
            res.addObject("disabledUsersCount", dao.count(new DAOListConfiguration(Restrictions.eq("enabled", false))));
        }
        if (getCtx().containsBean("jpm-dao-group")) {
            final HibernateCriteriaDAO dao = (HibernateCriteriaDAO) getCtx().getBean("jpm-dao-group");
            res.addObject("groupCount", dao.count(new DAOListConfiguration()));
        }
        setCurrentHome("security");
        return res;
    }

    /**
     * Serves a WithAttachment instance. The url lives under /static (public)
     * for backward compatibility, so authorization is checked here: the user
     * must be authenticated and able to access the entity, unless the entity
     * has publicAttachment=true. Any failure answers 404.
     */
    @RequestMapping(value = "/static/{entity}/{instanceId}/downloadAttachment")
    @ResponseBody
    public void downloadFileConverter(HttpServletResponse response, @PathVariable String entity, @PathVariable String instanceId, @RequestParam boolean download) throws IOException, PMException {
        final ContextualEntity ce = getJpm().getContextualEntity(entity);
        if (!ce.getEntity().isPublicAttachment() && !canAccessEntity(ce)) {
            LOG.debug("downloadAttachment DENIED entity={} instanceId={}", entity, instanceId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final Object object;
        try {
            // Through the service so the InstanceAccessGuard of the dao (if any) is applied
            object = getService().get(ce.getEntity(), ce.getContext(), instanceId).getObject();
        } catch (NotAuthorizedException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!(object instanceof WithAttachment)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final WithAttachment wa = (WithAttachment) object;
        if (download && !wa.isDownloadable()) {
            throw new PMException("jpm.not.downloadable");
        }
        response.setContentType(wa.getContentType());
        // Also name inline previews: mobile viewers may otherwise reuse the
        // shared URL filename "downloadAttachment" for different documents.
        response.setHeader("Content-Disposition", ContentDisposition.builder(download ? "attachment" : "inline")
                .filename(wa.getAttachmentName(), StandardCharsets.UTF_8)
                .build().toString());
        response.setHeader("Cache-Control", "private, no-store");
        if (wa.isExternalFile()) {
            try (FileInputStream is = new FileInputStream(new File(wa.getInternalFileName()))) {
                IOUtils.copy(is, response.getOutputStream());
            }
        } else {
            response.getOutputStream().write(wa.getAttachment());
        }
    }

    public WebApplicationContext getCtx() {
        return ctx;
    }

    public void setCtx(WebApplicationContext ctx) {
        this.ctx = ctx;
    }
}
