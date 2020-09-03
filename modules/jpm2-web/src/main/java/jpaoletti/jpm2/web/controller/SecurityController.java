package jpaoletti.jpm2.web.controller;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityContext;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @Autowired
    @Qualifier("jpm-dao-auth")
    private DAO authDAO;

    @Autowired
    @Qualifier("jpm-dao-group")
    private DAO groupDAO;

    @GetMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_RESET_PASSWORD + "}")
    public ModelAndView resetPassword(@PathVariable String instanceId) throws PMException {
        final UserDetails user = getSecurityService().resetPassword(getContext().getEntity(), getContext().getEntityContext(), getContext().getOperation(), instanceId);
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(instanceId, user), getContext()));
        return new ModelAndView("jpm-" + OP_RESET_PASSWORD);
    }

    @GetMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_PROFILE + "}")
    public ModelAndView profile(
        @PathVariable String instanceId,
        @RequestParam(required = false) String current,
        @RequestParam(required = false) String newpass) throws PMException {
        final Entity entity = getContext().getEntity();
        final IdentifiedObject iobject = getService().get(entity, getContext().getEntityContext(), getContext().getOperation(), instanceId);
        final UserDetails user = (UserDetails) iobject.getObject();
        if (!getUserDetails().getUsername().equals(user.getUsername())) {
            throw new NotAuthorizedException();
        }
        getContext().setEntityInstance(new EntityInstance(iobject, getContext()));
        if (current != null) {
            getSecurityService().changePassword(entity, getContext().getEntityContext(), getContext().getOperation(), instanceId, current, newpass);
            getContext().setGlobalMessage(MessageFactory.success("jpm.changePassword.success"));
        }
        final ModelAndView mav = new ModelAndView("jpm-" + OP_PROFILE);
        mav.addObject("currentUser", iobject.getObject());
        return mav;
    }

    @PostMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_PROFILE + "}")
    @ResponseBody
    public JPMPostResponse profilePost(
        @PathVariable String instanceId,
        @RequestParam(required = true) String current,
        @RequestParam(required = true) String newpass) {
        try {
            final Entity entity = getContext().getEntity();
            final IdentifiedObject iobject = getService().get(entity, getContext().getEntityContext(), getContext().getOperation(), instanceId);
            final UserDetails user = (UserDetails) iobject.getObject();
            if (!getUserDetails().getUsername().equals(user.getUsername())) {
                throw new NotAuthorizedException();
            }
            getContext().setEntityInstance(new EntityInstance(iobject, getContext()));
            getSecurityService().changePassword(entity, getContext().getEntityContext(), getContext().getOperation(), instanceId, current, newpass);
            return new JPMPostResponse(true, "/index", MessageFactory.success("jpm.changePassword.success"));
        } catch (NotAuthorizedException e) {
            getContext().getEntityMessages().add(MessageFactory.error("jpm.access.denied"));
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (PMException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }

    @GetMapping(value = "/jpm/security/i18n.js")
    public ModelAndView i18n() {
        final ModelAndView res = new ModelAndView("security.i18n");
        res.addObject("keys", getAuthDAO().list(new DAOListConfiguration()));
        return res;
    }

    @GetMapping(value = "/jpm/security/authorities.json", headers = "Accept=application/json")
    @ResponseBody
    public AuthoritiesResult getAuthorities(@RequestParam boolean readonly, @RequestParam String idGroup) {
        final AuthoritiesResult res = new AuthoritiesResult();
        final List<String> selected = new ArrayList<>();
        if (readonly) {
            final Group group = (Group) getGroupDAO().get(idGroup);
            selected.addAll(group.getAuthorities());
        }
        for (Entity e : getJpm().getEntityList()) {
            if (e.getClazz() != null && !"".equals(e.getClazz()) && !e.getAllOperations().isEmpty() && e.isAuthorizable()) {
                final AuthoritiesResultEntity are1 = new AuthoritiesResultEntity(e, null, selected);
                if (!are1.getOperations().isEmpty()) {
                    res.getEntities().add(are1);
                }
                for (EntityContext context : e.getContexts()) {
                    final AuthoritiesResultEntity are = new AuthoritiesResultEntity(e, context, selected);
                    if (!are.getOperations().isEmpty()) {
                        res.getEntities().add(are);
                    }
                }
            }
        }
        return res;
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public DAO getAuthDAO() {
        return authDAO;
    }

    public void setAuthDAO(DAO authDAO) {
        this.authDAO = authDAO;
    }

    public DAO getGroupDAO() {
        return groupDAO;
    }

    public void setGroupDAO(DAO groupDAO) {
        this.groupDAO = groupDAO;
    }

    /**
     * jpm.auth.operation.eee.ooo
     *
     */
    public static class AuthoritiesResult {

        private List<AuthoritiesResultEntity> entities = new ArrayList<>();

        public List<AuthoritiesResultEntity> getEntities() {
            return entities;
        }

        public void setEntities(List<AuthoritiesResultEntity> entities) {
            this.entities = entities;
        }
    }

    public static class AuthoritiesResultEntity {

        private String id;
        private String key; //jpm.auth.entity.eee
        private String name;
        private List<AuthoritiesResultOperation> operations = new ArrayList<>();

        public AuthoritiesResultEntity(Entity entity, EntityContext context, List<String> selected) {
            this.id = entity.getId();
            this.key = entity.getAuthKey(context);
            this.name = entity.getTitle() + (context == null ? "" : " (" + context.getId() + ")");
            for (Operation operation : entity.getAllOperations()) {
                if (operation.isAuthorizable()) {
                    final AuthoritiesResultOperation aro = new AuthoritiesResultOperation(entity, operation, context, selected);
                    if (selected.isEmpty() || selected.contains(aro.getKey())) {
                        this.operations.add(aro);
                    }
                }
            }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<AuthoritiesResultOperation> getOperations() {
            return operations;
        }

        public void setOperations(List<AuthoritiesResultOperation> operations) {
            this.operations = operations;
        }

    }

    public static class AuthoritiesResultOperation {

        private String id;
        private String key;
        private String name;
        private String icon;
        private List<AuthoritiesResultField> fields = new ArrayList<>();

        public AuthoritiesResultOperation(Entity entity, Operation operation, EntityContext context, List<String> selected) {
            this.id = operation.getId();
            this.key = operation.getAuthKey(entity, context);
            this.name = operation.getMessage(operation.getTitle(), entity.getTitle());
            this.icon = operation.getIcon();
            //Not implemented yet
            /*if (operation.isUseFields()) {
             for (Field f : entity.getAllFields(context == null ? null : context.getId())) {
             if (f.shouldDisplay(operation.getId())) {
             final String authKey = f.getAuthKey(entity, context, operation);
             if (selected.isEmpty() || selected.contains(authKey)) {
             fields.add(new AuthoritiesResultField(authKey, f.getTitle(entity)));
             }
             }
             }
             }*/
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<AuthoritiesResultField> getFields() {
            return fields;
        }

        public void setFields(List<AuthoritiesResultField> fields) {
            this.fields = fields;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class AuthoritiesResultField {

        private String key;
        private String name;

        public AuthoritiesResultField(String key, String name) {
            this.key = key;
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
