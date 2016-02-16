package jpaoletti.jpm2.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.service.AuditService;
import jpaoletti.jpm2.core.service.JPMService;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 *
 * @author jpaoletti
 */
public class PresentationManager {

    public static final String CONTEXT_SPLIT_STR = "[!]";
    public static final String CONTEXT_SEPARATOR = "!";

    private String title;
    private String subtitle;
    private String appversion;
    private String contact;
    private String cssMode;
    private AuditService auditService;
    private JPMService service;
    private CustomLoader customLoader;

    @Autowired(required = false)
    private Map<String, Entity> entities;

    @Autowired(required = false)
    private List<Entity> entityList;

    public PresentationManager() {
        this.title = "JPM2";
        this.subtitle = "Java Presentation Manager 2";
    }

    protected void customLoad() throws Exception {
        if (getCustomLoader() != null) {
            getCustomLoader().execute(this);
        }
    }

    /**
     * Formatting helper for startup
     *
     * @param s1 Text
     * @param s2 Extra description
     * @param symbol Status symbol
     */
    public void logItem(String s1, String s2, String symbol) {
        JPMUtils.getLogger().info(String.format("(%s) %-25s %s", symbol, s1, (s2 != null) ? s2 : ""));
    }

    /**
     * GETTERS & SETTERS
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCssMode() {
        return cssMode;
    }

    public void setCssMode(String cssMode) {
        this.cssMode = cssMode;
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, Entity> entities) {
        this.entities = entities;
    }

    public CustomLoader getCustomLoader() {
        return customLoader;
    }

    public void setCustomLoader(CustomLoader customLoader) {
        this.customLoader = customLoader;
    }

    public Entity getEntity(String entityId) throws EntityNotFoundException {
        if (!getEntities().containsKey(entityId)) {
            throw new EntityNotFoundException(entityId);
        }
        return getEntities().get(entityId);
    }

    /**
     * Getter for entities based on numericId.
     *
     * @param numericId
     * @return list of entities with the given numeric id
     */
    public List<Entity> getEntities(Integer numericId) {
        final List<Entity> res = new ArrayList<>();
        for (Map.Entry<String, Entity> entry : getEntities().entrySet()) {
            final Entity entity = entry.getValue();
            if (entity.getNumericId() != null && numericId.equals(entity.getNumericId())) {
                try {
                    entity.checkAuthorization();
                    res.add(entity);
                } catch (NotAuthorizedException ex) {
                }
            }
        }
        return res;
    }

    public ContextualEntity getContextualEntity(String id) throws EntityNotFoundException {
        if (!id.contains(CONTEXT_SEPARATOR)) {
            return new ContextualEntity(getEntity(id), null);
        } else {
            final String[] split = id.split(CONTEXT_SPLIT_STR);
            return new ContextualEntity(getEntity(split[0]), split[1]);
        }
    }

    public AuditService getAuditService() {
        return auditService;
    }

    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    public void audit(String s) {
        audit(null, null, null, s);
    }

    public void audit(Entity entity, Operation operation, IdentifiedObject iobject) {
        if (getAuditService() != null) {
            getAuditService().register(entity, operation, iobject);
        }
    }

    public void audit(Entity entity, Operation operation, IdentifiedObject iobject, String s) {
        if (getAuditService() != null) {
            getAuditService().register(entity, operation, iobject, s);
        }
    }

    public JPMService getService() {
        return service;
    }

    public void setService(JPMService service) {
        this.service = service;
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public MessageSource getMessageSource() {
        return (MessageSource) JPMUtils.getApplicationContext().getBean("messageSource");
    }
}
