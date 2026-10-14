package jpaoletti.jpm2.core.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.dao.JPADAO;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.persistent.JpmMenu;
import jpaoletti.jpm2.core.model.persistent.JpmMenuType;
import jpaoletti.jpm2.core.service.AuthorizationService;
import jpaoletti.jpm2.core.service.SysparamService;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Dynamic menu: reads the jpm_menu table, keeps it cached and builds the menu
 * visible to the current user.
 *
 * <p>The items are cached for {@link #getCacheSeconds()} (60 by default) and the
 * cache is cleared when an item is saved or deleted through the jpmMenu DAO. The
 * expiration covers changes made in another node or directly in the database.
 *
 * <p>Declared in {@code entities/jpmMenu.xml}.
 *
 * @author jpaoletti
 */
public class JpmMenuService {

    public static final String GENERATED_GROUP_LABEL = "jpm.menu.generated";
    private static final int MAX_DEPTH = 10;

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PresentationManager jpm;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired(required = false)
    private SysparamService sysparamService;
    @Autowired
    @Qualifier(value = "dao-jpmMenu")
    private JPADAO menuDAO;

    private int cacheSeconds = 60;
    private volatile Snapshot snapshot;

    /**
     * @return true when the {@code jpm.menu.dynamic} sysparam is on.
     */
    public boolean isEnabled() {
        if (sysparamService == null) {
            return false;
        }
        try {
            return sysparamService.flag(JpmMenuSysparamModule.DYNAMIC);
        } catch (Exception e) {
            JPMUtils.getLogger().warn("Dynamic menu sysparam could not be read", e);
            return false;
        }
    }

    /**
     * @return the menu visible to the current user, or null when the menu
     * table is empty (the static menu is used so that the menu can still be
     * administrated). An anonymous user gets an empty menu.
     */
    public List<JpmMenuNode> getVisibleMenu() {
        final List<JpmMenuItem> items = getMenu();
        if (items.isEmpty()) {
            return null;
        }
        if (!isAuthenticated()) {
            return new ArrayList<>();
        }
        return JpmMenuTree.visible(items, new UserMenuAccess());
    }

    /**
     * @return all the root items (active or not, valid or not), from the
     * cache.
     */
    public List<JpmMenuItem> getMenu() {
        Snapshot s = snapshot;
        if (s == null || s.isExpired(cacheSeconds)) {
            s = new Snapshot(load());
            snapshot = s;
        }
        return s.items;
    }

    /**
     * Clears the cache. Inside a transaction it is cleared again after the
     * commit, so that a request running meanwhile doesn't cache the old rows.
     */
    public void invalidate() {
        snapshot = null;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    snapshot = null;
                }
            });
        }
    }

    /**
     * Loads the menu in a session of its own: the menu is read from an
     * interceptor, where there may be no session, and the cached copy must
     * not depend on it.
     */
    protected List<JpmMenuItem> load() {
        final Session session = sessionFactory.openSession();
        try {
            final List<JpmMenu> rows = session.createQuery("from JpmMenu", JpmMenu.class).list();
            final Map<Long, List<JpmMenu>> byParent = new LinkedHashMap<>();
            for (JpmMenu row : rows) {
                final Long parentId = row.getParent() == null ? null : row.getParent().getId();
                byParent.computeIfAbsent(parentId, k -> new ArrayList<>()).add(row);
            }
            final List<JpmMenuItem> res = toItems(byParent, null, 0);
            JPMUtils.getLogger().debug("Dynamic menu loaded: {} rows", rows.size());
            return res;
        } catch (Exception e) {
            JPMUtils.getLogger().error("Dynamic menu could not be loaded", e);
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }

    private List<JpmMenuItem> toItems(Map<Long, List<JpmMenu>> byParent, Long parentId, int depth) {
        final List<JpmMenuItem> res = new ArrayList<>();
        final List<JpmMenu> rows = byParent.get(parentId);
        if (rows == null || depth > MAX_DEPTH) {
            return res;
        }
        rows.sort(Comparator.comparing((JpmMenu m) -> m.getSortOrder() == null ? 0 : m.getSortOrder())
                .thenComparing(m -> m.getId() == null ? 0L : m.getId()));
        for (JpmMenu row : rows) {
            final List<JpmMenuItem> children = row.getType() == JpmMenuType.GROUP
                    ? toItems(byParent, row.getId(), depth + 1) : null;
            final String invalid = invalidReason(row.getType(), row.getEntity(), row.getOperation(), row.getUrl());
            if (invalid != null && row.isActive()) {
                JPMUtils.getLogger().warn("Dynamic menu item {} ignored: {}", row.getId(), invalid);
            }
            final String operationPath = invalid == null && row.getType() == JpmMenuType.ENTITY
                    ? operationPath(row.getEntity(), row.getOperation()) : null;
            res.add(new JpmMenuItem(row.getId(), row.getType(), row.getLabel(), row.getIcon(), row.getEntity(),
                    row.getOperation(), operationPath, row.getUrl(), row.getAuthorities(), row.isActive(), invalid, children)
                    .withUserCondition(row.getUserCondition()));
        }
        return res;
    }

    private String operationPath(String entity, String operation) {
        try {
            final String op = StringUtils.defaultIfBlank(StringUtils.trimToNull(operation), JpmMenuItem.DEFAULT_OPERATION);
            return jpm.getContextualEntity(entity.trim()).getEntity().getOperationWithoutAuth(op, null).getPathId();
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * @return why an item with this data can't be shown, null when it is
     * valid.
     */
    public String invalidReason(JpmMenuType type, String entity, String operation, String url) {
        if (type == JpmMenuType.ENTITY) {
            if (StringUtils.isBlank(entity)) {
                return "no entity";
            }
            try {
                final ContextualEntity ce = jpm.getContextualEntity(entity.trim());
                final String op = StringUtils.defaultIfBlank(StringUtils.trimToNull(operation), JpmMenuItem.DEFAULT_OPERATION);
                if (ce.getEntity().getOperationWithoutAuth(op, null) == null) {
                    return "unknown operation " + entity.trim() + "/" + op;
                }
            } catch (EntityNotFoundException e) {
                return "unknown entity " + entity.trim();
            }
        } else if (type == JpmMenuType.URL) {
            if (StringUtils.isBlank(url)) {
                return "no url";
            }
            if (JpmMenuTree.hasUnsupportedScheme(url.trim())) {
                return "unsupported url " + url.trim();
            }
        }
        return null;
    }

    /**
     * Creates a group with an item for each entity with a list operation that
     * is not weak and is not in the menu yet.
     *
     * @return the number of items created
     */
    public int generateFromEntities() {
        final Set<String> existing = new HashSet<>();
        int maxRootOrder = 0;
        for (Object o : menuDAO.list(menuDAO.build())) {
            final JpmMenu m = (JpmMenu) o;
            if (m.getType() == JpmMenuType.ENTITY && m.getEntity() != null) {
                existing.add(m.getEntity().trim());
            }
            if (m.getParent() == null && m.getSortOrder() != null) {
                maxRootOrder = Math.max(maxRootOrder, m.getSortOrder());
            }
        }
        final List<Entity> entities = new ArrayList<>();
        for (Entity entity : jpm.getEntityList()) {
            if (entity.getOwner() == null && !existing.contains(entity.getId())
                    && entity.getOperationWithoutAuth(JpmMenuItem.DEFAULT_OPERATION, null) != null) {
                entities.add(entity);
            }
        }
        if (entities.isEmpty()) {
            return 0;
        }
        entities.sort(Comparator.comparing(e -> StringEscapeUtils.unescapeHtml4(e.getPluralTitle()).toLowerCase()));
        final JpmMenu group = new JpmMenu();
        group.setType(JpmMenuType.GROUP);
        group.setLabel(GENERATED_GROUP_LABEL);
        group.setIcon("fas fa-boxes");
        group.setSortOrder(maxRootOrder + 10);
        menuDAO.save(group);
        int order = 0;
        for (Entity entity : entities) {
            final JpmMenu item = new JpmMenu();
            item.setParent(group);
            item.setType(JpmMenuType.ENTITY);
            item.setEntity(entity.getId());
            item.setOperation(JpmMenuItem.DEFAULT_OPERATION);
            final String key = "top.menu." + entity.getId();
            item.setLabel(hasMessage(key) ? key : null);
            item.setSortOrder(order += 10);
            menuDAO.save(item);
        }
        invalidate();
        return entities.size();
    }

    /**
     * @return the jstree JSON of the whole menu (inactive and invalid items
     * included and marked) for the preview operation.
     */
    public String treeJson() {
        return treeJson(getMenu(), new UserMenuAccess()).toString();
    }

    private JSONArray treeJson(List<JpmMenuItem> items, UserMenuAccess access) {
        final JSONArray res = new JSONArray();
        for (JpmMenuItem item : items) {
            final StringBuilder text = new StringBuilder();
            text.append("<span class=\"badge bg-light text-dark border me-1\">").append(item.getType()).append("</span>");
            text.append(item.getType() == JpmMenuType.SEPARATOR ? "&mdash;&mdash;&mdash;" : access.labelHtml(item));
            if (item.getType() == JpmMenuType.ENTITY) {
                text.append(" <span class=\"text-muted\">").append(escape(item.getEntity() + "/" + item.getOperation())).append("</span>");
            } else if (item.getType() == JpmMenuType.URL) {
                text.append(" <span class=\"text-muted\">").append(escape(item.getUrl())).append("</span>");
            }
            if (!item.getAuthorities().isEmpty()) {
                text.append(" <span class=\"text-info\"><i class=\"fas fa-lock\"></i> ").append(escape(String.join(", ", item.getAuthorities()))).append("</span>");
            }
            if (item.getUserCondition() != null) {
                text.append(" <span class=\"text-warning\"><i class=\"fas fa-user\"></i> ").append(escape(item.getUserCondition())).append("</span>");
            }
            if (!item.isActive()) {
                text.append(" <span class=\"badge bg-secondary\">").append(escape(jpm.getMessage("jpm.menu.preview.inactive"))).append("</span>");
            }
            if (!item.isValid()) {
                text.append(" <span class=\"badge bg-danger\" title=\"").append(escape(item.getInvalidReason())).append("\">")
                        .append(escape(jpm.getMessage("jpm.menu.preview.invalid"))).append("</span>");
            }
            final JSONObject node = new JSONObject();
            node.put("id", "m:" + item.getId());
            node.put("text", text.toString());
            node.put("icon", item.getIcon() != null ? escape(item.getIcon())
                    : (item.getType() == JpmMenuType.GROUP ? "fas fa-folder" : "far fa-file"));
            node.put("state", new JSONObject().put("opened", true));
            node.put("children", treeJson(item.getChildren(), access));
            res.put(node);
        }
        return res;
    }

    protected boolean isAuthenticated() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    private boolean hasMessage(String key) {
        return !key.equals(jpm.getMessage(key));
    }

    private static String escape(String s) {
        return s == null ? "" : StringEscapeUtils.escapeHtml4(s);
    }

    /**
     * Access of the logged user, with the same checks the entity and the
     * operation do.
     */
    private class UserMenuAccess implements JpmMenuAccess {

        @Override
        public boolean hasAnyAuthority(List<String> authorities) {
            return authorizationService.userHasRole(String.join(",", authorities));
        }

        @Override
        public boolean canAccess(String entity, String operation) {
            try {
                final ContextualEntity ce = jpm.getContextualEntity(entity);
                ce.checkAuthorization();
                final Operation op = ce.getEntity().getOperationWithoutAuth(operation, null);
                if (op == null) {
                    return false;
                }
                op.checkAuthorization(ce.getEntity(), ce.getContext());
                return true;
            } catch (EntityNotFoundException | NotAuthorizedException e) {
                return false;
            }
        }

        @Override
        public String resolveUrl(String url) {
            return JpmMenuTree.resolveUserPlaceholders(url, this::userProperty);
        }

        @Override
        public boolean matchesUserCondition(String condition) {
            return JpmMenuTree.matchesUserCondition(condition, this::userProperty);
        }

        private Object userProperty(String property) {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            final Object principal = auth == null ? null : auth.getPrincipal();
            try {
                return JPMUtils.get(principal, property);
            } catch (Exception e) {
                JPMUtils.getLogger().debug("Menu user property {} could not be read", property, e);
                return null;
            }
        }

        @Override
        public String labelHtml(JpmMenuItem item) {
            if (item.getLabel() != null) {
                final String text = jpm.getMessage(item.getLabel());
                return item.getLabel().equals(text) ? escape(item.getLabel()) : text;
            }
            if (item.getType() == JpmMenuType.ENTITY && item.getEntity() != null) {
                final String key = "top.menu." + item.getEntity().replace('!', '.');
                final String text = jpm.getMessage(key);
                if (!key.equals(text)) {
                    return text;
                }
                try {
                    final Entity entity = jpm.getContextualEntity(item.getEntity()).getEntity();
                    final String title = entity.getPluralTitle();
                    if (!title.startsWith("jpm.entity.title.")) {
                        return title;
                    }
                } catch (EntityNotFoundException e) {
                    // falls back to the entity id
                }
                return escape(item.getEntity());
            }
            if (item.getType() == JpmMenuType.URL) {
                return escape(item.getUrl());
            }
            return "";
        }
    }

    public int getCacheSeconds() {
        return cacheSeconds;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }

    private static class Snapshot {

        private final List<JpmMenuItem> items;
        private final long created = System.currentTimeMillis();

        Snapshot(List<JpmMenuItem> items) {
            this.items = items;
        }

        boolean isExpired(int seconds) {
            return seconds > 0 && System.currentTimeMillis() - created > seconds * 1000L;
        }
    }
}
