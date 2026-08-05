package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.cache.GeneralCache;
import jpaoletti.jpm2.core.log.DebugLog;
import jpaoletti.jpm2.core.crypto.SysparamCipher;
import jpaoletti.jpm2.core.dao.DefaultJPADAO;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
import jpaoletti.jpm2.core.model.persistent.Sysparam;
import jpaoletti.jpm2.core.model.persistent.SysparamGroup;
import jpaoletti.jpm2.core.sysparam.SysparamCatalog;
import jpaoletti.jpm2.core.sysparam.SysparamDef;
import jpaoletti.jpm2.core.sysparam.SysparamFamily;
import jpaoletti.jpm2.core.sysparam.SysparamHealthItem;
import jpaoletti.jpm2.core.sysparam.SysparamHealthReport;
import jpaoletti.jpm2.core.sysparam.SysparamType;
import jpaoletti.jpm2.util.JPMUtils;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core sysparam service: resolves typed values (cache → DB override → catalog default),
 * encrypts/decrypts SECRET values, validates and audits writes, and exposes dynamic
 * families verbatim. Caches non-secret values through the {@link CacheService} region
 * {@value #CACHE_REGION}. Defaults come from the {@link SysparamCatalog} (code-declared).
 *
 * @author jpaoletti
 */
public class SysparamService extends JPMServiceBase {

    public static final String CACHE_REGION = "sysparam";

    /**
     * Sysparam key that drives the global {@link DebugLog} level (INTEGER 0-3): declaring a
     * param with this key turns the sysparam admin into the live control surface for runtime
     * debug logging. Keys of the form {@code debug.<channel>} drive per-channel levels.
     * The value is pushed to DebugLog on every write and re-seeded on boot, so changing it
     * from the setValue screen takes effect immediately without a restart.
     */
    public static final String DEBUG_KEY = "debug";
    private static final String DEBUG_CHANNEL_PREFIX = "debug.";

    @Autowired
    private SysparamCatalog catalog;

    @Autowired
    private SysparamCipher cipher;

    @Autowired
    private CacheService cacheService;

    @Autowired
    @Qualifier(value = "dao-sysparam")
    private DefaultJPADAO sysparamDAO;

    /** Optional: present only in apps that register the sysparamGroup entity (tree styling). */
    @Autowired(required = false)
    @Qualifier(value = "dao-sysparamGroup")
    private DefaultJPADAO sysparamGroupDAO;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Builds the catalog and seeds any managed parameter that has no DB row yet, so all
     * known parameters appear in the admin grid. Secret defaults are NOT persisted (left
     * null) to avoid storing plaintext secrets.
     */
    public void init() {
        JPMUtils.getLogger().info("Iniciando servicio de sysparams");
        catalog.build();
        if (!cipher.isEnabled()) {
            JPMUtils.getLogger().warn("Sysparam cipher deshabilitado (sysparam.secret.key vacio): los parametros SECRET no podran guardarse ni leerse hasta configurar la clave");
        }
        seedMissingDefaults();
        seedMissingGroups();
        seedDebugLog();
    }

    /**
     * Pushes the persisted {@code debug} / {@code debug.<channel>} levels into {@link DebugLog}
     * at boot so a level left on before a restart is restored. Best-effort: a failure here must
     * never block startup.
     */
    private void seedDebugLog() {
        try {
            final Session session = sessionFactory.openSession();
            try {
                final List<Sysparam> rows = session.createQuery("from Sysparam", Sysparam.class).list();
                for (final Sysparam row : rows) {
                    applyDebugLevel(row.getKey(), row.getValue());
                }
            } finally {
                session.close();
            }
        } catch (final Exception e) {
            JPMUtils.getLogger().warn("No se pudo inicializar DebugLog desde sysparam", e);
        }
    }

    /**
     * Routes a {@code debug} / {@code debug.<channel>} value to {@link DebugLog}. No-op for any
     * other key, so it is safe to call on every write.
     */
    private void applyDebugLevel(String key, String rawValue) {
        if (DEBUG_KEY.equals(key)) {
            DebugLog.setGlobalLevel(parseLevel(rawValue));
        } else if (key != null && key.startsWith(DEBUG_CHANNEL_PREFIX)) {
            DebugLog.setChannelLevel(key.substring(DEBUG_CHANNEL_PREFIX.length()), parseLevel(rawValue));
        }
    }

    private static int parseLevel(String rawValue) {
        if (rawValue == null) {
            return 0;
        }
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    private void seedMissingDefaults() {
        final Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            final List<Sysparam> existing = session.createQuery("from Sysparam", Sysparam.class).list();
            final Set<String> present = existing.stream().map(Sysparam::getKey).collect(Collectors.toSet());
            int seeded = 0;
            for (SysparamDef<?> def : catalog.allDefs().values()) {
                if (!present.contains(def.getKey())) {
                    final Sysparam p = new Sysparam();
                    p.setKey(def.getKey());
                    p.setType(def.getType());
                    p.setGroup(def.getGroup());
                    p.setCached(def.isCached());
                    p.setValue(def.isSecret() ? null : def.getDefaultRaw());
                    session.save(p);
                    seeded++;
                }
            }
            tx.commit();
            if (seeded > 0) {
                JPMUtils.getLogger().info("Sysparam: sembrados " + seeded + " parametros nuevos del catalogo");
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JPMUtils.getLogger().warn("No se pudieron sembrar los sysparams del catalogo (se resolveran por default hasta recargar)", e);
        } finally {
            session.close();
        }
    }

    /**
     * Seeds a {@link SysparamGroup} presentation row for every distinct parameter group that
     * has none yet (default folder icon, collapsed), so the admin sees all groups ready to
     * style. No-op when the group entity is not wired.
     */
    private void seedMissingGroups() {
        if (sysparamGroupDAO == null) {
            return;
        }
        final Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            final java.util.Set<String> present = new java.util.HashSet<>(
                    session.createQuery("select g.name from SysparamGroup g", String.class).list());
            final List<?> groupsRaw = session.createNativeQuery(
                    "SELECT DISTINCT param_group FROM jpm_sysparam WHERE param_group IS NOT NULL").list();
            int seeded = 0;
            for (Object o : groupsRaw) {
                final String g = String.valueOf(o);
                if (!g.isEmpty() && !present.contains(g)) {
                    final SysparamGroup sg = new SysparamGroup();
                    sg.setName(g);
                    sg.setIcon("fas fa-folder");
                    sg.setCollapsed(true);
                    sg.setSortOrder(0);
                    session.save(sg);
                    seeded++;
                }
            }
            tx.commit();
            if (seeded > 0) {
                JPMUtils.getLogger().info("Sysparam: sembrados " + seeded + " grupos nuevos");
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            JPMUtils.getLogger().warn("No se pudieron sembrar los grupos de sysparam", e);
        } finally {
            session.close();
        }
    }

    /** @return the group presentation overlays keyed by group name (empty if not wired). */
    public Map<String, SysparamGroup> groups() {
        if (sysparamGroupDAO == null) {
            return java.util.Collections.emptyMap();
        }
        return inSession(() -> {
            final Map<String, SysparamGroup> map = new LinkedHashMap<>();
            for (Object o : sysparamGroupDAO.list(sysparamGroupDAO.build())) {
                final SysparamGroup g = (SysparamGroup) o;
                map.put(g.getName(), g);
            }
            return map;
        });
    }

    // --- Resolution ---

    /**
     * Resolves the effective raw value: cache (non-secret) → DB override (decrypted if
     * secret) → catalog default. Returns null if unset and no default.
     */
    public String getRaw(String key) {
        final boolean cacheable = catalog.isCacheable(key);
        if (cacheable) {
            final String cached = region().get(key);
            if (cached != null) {
                return cached;
            }
        }
        final Sysparam row = findRow(key);
        String raw;
        if (row != null && row.getValue() != null) {
            raw = catalog.isSecret(key) ? decryptQuietly(row.getValue()) : row.getValue();
        } else {
            final SysparamDef<?> def = catalog.defFor(key);
            raw = (def != null) ? def.getDefaultRaw() : null;
        }
        if (raw != null && cacheable) {
            region().set(key, raw);
        }
        return raw;
    }

    /**
     * Typed, catalog-driven access. Falls back to the declared default when unset or when
     * the stored value fails to parse (logging the parse error instead of failing silently).
     */
    public <T> T get(SysparamDef<T> def) {
        final String raw = getRaw(def.getKey());
        if (raw == null) {
            return def.getDefault();
        }
        try {
            return def.parse(raw);
        } catch (Exception e) {
            JPMUtils.getLogger().error("Sysparam '" + def.getKey() + "' con valor invalido '" + raw + "'; se usa el default", e);
            return def.getDefault();
        }
    }

    public boolean flag(SysparamDef<Boolean> def) {
        final Boolean value = get(def);
        return value != null && value;
    }

    // --- ConfigService-compatible getters (used by the compatibility bridge) ---

    public String getString(String key, String def) {
        final String raw = getRaw(key);
        return raw != null ? raw : def;
    }

    public Integer getInt(String key, Integer def) {
        try {
            final String raw = getRaw(key);
            return raw != null ? Integer.valueOf(raw.trim()) : def;
        } catch (Exception e) {
            return def;
        }
    }

    public Long getLong(String key, Long def) {
        try {
            final String raw = getRaw(key);
            return raw != null ? Long.valueOf(raw.trim()) : def;
        } catch (Exception e) {
            return def;
        }
    }

    public BigDecimal getBigDecimal(String key, BigDecimal def) {
        try {
            final String raw = getRaw(key);
            return raw != null ? new BigDecimal(raw.trim()) : def;
        } catch (Exception e) {
            return def;
        }
    }

    public Double getDouble(String key, Double def) {
        try {
            final String raw = getRaw(key);
            return raw != null ? Double.valueOf(raw.trim()) : def;
        } catch (Exception e) {
            return def;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        final String raw = getRaw(key);
        if (raw == null) {
            return def;
        }
        return StringUtils.equalsAnyIgnoreCase(raw.trim(), "true", "yes", "y", "1", "on");
    }

    /**
     * Reads a whole dynamic family (all keys with the given prefix) verbatim. Secret
     * members are decrypted. Intended for passthrough (e.g. terminal-* to a POS).
     */
    public Map<String, String> family(String prefix) {
        return inSession(() -> {
            final JPADAOListConfiguration cfg = sysparamDAO.build();
            cfg.withPredicate((cb, root) -> cb.like(root.<String>get("key"), prefix + "%"));
            final Map<String, String> result = new LinkedHashMap<>();
            for (Object o : sysparamDAO.list(cfg)) {
                final Sysparam p = (Sysparam) o;
                String value = p.getValue();
                if (value != null && catalog.isSecret(p.getKey())) {
                    value = decryptQuietly(value);
                }
                result.put(p.getKey(), value);
            }
            return result;
        });
    }

    // --- Mutation ---

    /**
     * Sets a value: validates against the catalog definition, encrypts if secret, records
     * a redacted history entry and evicts the cache. Creates the row if absent.
     */
    @Transactional(rollbackFor = Exception.class)
    public void set(String key, String rawValue, String user) throws PMException {
        final SysparamDef<?> def = catalog.defFor(key);
        if (def != null) {
            final String error = def.validate(rawValue);
            if (error != null) {
                throw new PMException("Valor invalido para '" + key + "': " + error);
            }
        }
        Sysparam row = findRow(key);
        final boolean isNew = (row == null);
        // Decide secrecy defensively from BOTH the catalog and the persisted row type: if
        // either says SECRET, treat it as secret. This keeps a value from ever being stored or
        // audited in plaintext when the catalog momentarily does not classify the key as secret
        // (e.g. a stale deploy or a catalog not yet built) but the seeded row already knows it
        // is. Once secret, a key stays secret.
        final boolean secret = catalog.isSecret(key) || (row != null && row.isSecret());
        if (isNew) {
            row = new Sysparam();
            row.setKey(key);
            final SysparamFamily family = catalog.familyFor(key);
            row.setType(def != null ? def.getType() : (family != null ? family.getMemberType() : SysparamType.STRING));
            row.setGroup(def != null ? def.getGroup() : (family != null ? family.getGroup() : "general"));
            row.setCached(def != null ? def.isCached() : (family == null || family.isCached()));
        }
        // Self-heal the row type so secrecy (now carried by the type) is authoritative next
        // time, closing the window where a stale row type could leak a secret in plaintext.
        if (secret && row.getType() != SysparamType.SECRET) {
            row.setType(SysparamType.SECRET);
        }
        final String stored = (secret && rawValue != null) ? cipher.encrypt(rawValue) : rawValue;
        row.setValue(stored);
        row.setUpdatedBy(user);
        row.setUpdatedAt(new Date());
        if (isNew) {
            sysparamDAO.save(row);
        } else {
            sysparamDAO.update(row);
        }
        // Change history is recorded by the standard JPM audit (detailed audit on the
        // sysparam entity), so no separate history row is written here.
        region().del(key);
        // Debug logging control surface: a write to debug/debug.<channel> takes effect live.
        applyDebugLevel(key, rawValue);
    }

    /** Resets a managed parameter to its catalog default (persisting the default value). */
    @Transactional(rollbackFor = Exception.class)
    public void resetToDefault(String key, String user) throws PMException {
        final SysparamDef<?> def = catalog.defFor(key);
        set(key, def != null ? def.getDefaultRaw() : null, user);
    }

    // --- Cache control ---

    public void clearCache() {
        region().clear();
    }

    public void clearCache(String key) {
        region().del(key);
    }

    public SysparamCatalog getCatalog() {
        return catalog;
    }

    /**
     * @return true when the key is owned by Sysparam (a catalog definition or a dynamic
     * family). Cheap, in-memory check used by the compatibility bridge to decide whether
     * to resolve a key here or defer to the legacy config store.
     */
    public boolean isKnown(String key) {
        return catalog.isManaged(key);
    }

    public boolean isCipherEnabled() {
        return cipher.isEnabled();
    }

    /**
     * Decrypts a secret's stored value for the reveal UI. Independent of the catalog (the
     * caller has already established the parameter is secret via its type), so it reveals the
     * real value even if the catalog does not currently classify the key as secret. Plaintext
     * input is returned unchanged; returns null when it cannot be decrypted.
     */
    public String revealSecretValue(String stored) {
        return decryptQuietly(stored);
    }

    // --- Health ---

    /**
     * Reconciles the code-declared catalog against the stored rows and returns a read-only
     * diagnostic report: required parameters without a value, stored values that fail their
     * definition's validation, SECRET values kept in plaintext, orphan (undeclared) rows and
     * deprecated parameters still set. Never mutates anything.
     */
    public SysparamHealthReport health() {
        final SysparamHealthReport report = new SysparamHealthReport();
        report.setCipherEnabled(cipher.isEnabled());
        final Map<String, SysparamDef<?>> defs = catalog.allDefs();
        report.setTotalDefs(defs.size());

        final Map<String, Sysparam> rows = new LinkedHashMap<>();
        for (Object o : sysparamDAO.list(sysparamDAO.build())) {
            final Sysparam p = (Sysparam) o;
            rows.put(p.getKey(), p);
        }
        report.setTotalRows(rows.size());

        boolean anySecret = false;
        for (SysparamDef<?> def : defs.values()) {
            final String key = def.getKey();
            final Sysparam row = rows.get(key);
            final boolean hasStored = row != null && row.getValue() != null;
            if (def.isSecret()) {
                anySecret = true;
            }
            if (def.isRequired() && StringUtils.isBlank(safeGetRaw(key))) {
                report.add(SysparamHealthItem.Severity.ERROR, "missingRequired", key,
                        "Required parameter has no stored value and no default");
            }
            if (def.isDeprecated() && hasStored) {
                report.add(SysparamHealthItem.Severity.WARNING, "deprecatedSet", key,
                        "Deprecated parameter still has a stored value");
            }
            if (hasStored) {
                final String effective = def.isSecret() ? decryptQuietly(row.getValue()) : row.getValue();
                if (effective == null && def.isSecret()) {
                    report.add(SysparamHealthItem.Severity.WARNING, "secretUnreadable", key,
                            "Secret value could not be decrypted (cipher key mismatch?)");
                } else if (effective != null) {
                    final String error = def.validate(effective);
                    if (error != null) {
                        report.add(SysparamHealthItem.Severity.ERROR, "validationFail", key, error);
                    }
                }
            }
        }

        for (Sysparam row : rows.values()) {
            final String key = row.getKey();
            if (!catalog.isManaged(key)) {
                report.add(SysparamHealthItem.Severity.INFO, "orphan", key,
                        "Stored parameter is not declared in the catalog (legacy/free)");
            }
            final boolean secret = catalog.isSecret(key) || row.isSecret();
            if (secret && row.getValue() != null && !cipher.isEncrypted(row.getValue())) {
                report.add(SysparamHealthItem.Severity.ERROR, "plaintextSecret", key,
                        "Secret value is stored in plaintext (not encrypted)");
            }
        }

        if (!cipher.isEnabled() && anySecret) {
            report.add(SysparamHealthItem.Severity.ERROR, "cipherDisabled", "-",
                    "Cipher is disabled (sysparam.secret.key not set) but SECRET parameters exist");
        }
        return report;
    }

    private String safeGetRaw(String key) {
        try {
            return getRaw(key);
        } catch (Exception e) {
            return null;
        }
    }

    // --- Tree view ---

    /**
     * Builds a jstree JSON model of the whole catalog for the read-only tree view: one parent
     * node per {@code group}, one leaf per parameter (key + masked, truncated value). Leaves
     * carry their param id in {@code data.pid} so the UI can open its setValue. The JSON is
     * produced with org.json so values containing quotes/HTML are escaped safely.
     */
    public String treeJson() {
        final java.util.Map<String, java.util.List<Sysparam>> byGroup = new java.util.TreeMap<>();
        for (Object o : sysparamDAO.list(sysparamDAO.build())) {
            final Sysparam p = (Sysparam) o;
            final String g = StringUtils.isBlank(p.getGroup()) ? "general" : p.getGroup();
            byGroup.computeIfAbsent(g, k -> new java.util.ArrayList<>()).add(p);
        }
        final Map<String, SysparamGroup> styles = groups();
        // Order groups by their configured sortOrder (default 0), ties broken by name.
        final java.util.List<String> ordered = new java.util.ArrayList<>(byGroup.keySet());
        ordered.sort(java.util.Comparator
                .comparingInt((String g) -> {
                    final SysparamGroup sg = styles.get(g);
                    return (sg != null && sg.getSortOrder() != null) ? sg.getSortOrder() : 0;
                })
                .thenComparing(g -> g));
        final org.json.JSONArray root = new org.json.JSONArray();
        for (String g : ordered) {
            final java.util.List<Sysparam> params = byGroup.get(g);
            params.sort(java.util.Comparator.comparing(p -> p.getKey() == null ? "" : p.getKey()));
            final org.json.JSONArray children = new org.json.JSONArray();
            for (Sysparam p : params) {
                // Enrich each leaf with the metadata the inline editor needs (type/secret/allowed/
                // default/current), mirroring EntityParameterTree so the tree edits in place instead
                // of navigating to setValue. The catalog is the source of truth; fall back to the row.
                final SysparamDef<?> def = catalog.defFor(p.getKey());
                final boolean secret = catalog.isSecret(p.getKey()) || p.isSecret();
                final String type = (def != null) ? def.getType().name()
                        : (p.getType() != null ? p.getType().name() : SysparamType.STRING.name());
                final org.json.JSONArray allowed = new org.json.JSONArray();
                if (def != null) {
                    for (String v : def.getAllowedValues()) {
                        allowed.put(v);
                    }
                }
                final org.json.JSONObject data = new org.json.JSONObject();
                data.put("pid", p.getId());
                data.put("name", p.getKey());
                data.put("secret", secret);
                data.put("type", type);
                data.put("allowed", allowed);
                data.put("def", def != null ? def.getDefaultRaw() : org.json.JSONObject.NULL);
                // Never expose a secret's current value; show the plain value otherwise.
                data.put("cur", secret ? "" : (p.getValue() == null ? "" : p.getValue()));

                final org.json.JSONObject leaf = new org.json.JSONObject();
                leaf.put("id", "p:" + p.getId());
                leaf.put("text", escapeHtml(p.getKey())
                        + " <span class=\"text-muted\">= " + escapeHtml(truncate(p.getDisplayValue(), 60)) + "</span>");
                leaf.put("icon", secret ? "fas fa-key" : "fas fa-tag");
                leaf.put("data", data);
                children.put(leaf);
            }
            final SysparamGroup sg = styles.get(g);
            final String iconClass = (sg != null && StringUtils.isNotBlank(sg.getIcon())) ? sg.getIcon() : "fas fa-folder";
            final String css = (sg != null && StringUtils.isNotBlank(sg.getStyle())) ? sg.getStyle() : null;
            final String label = escapeHtml((sg != null && StringUtils.isNotBlank(sg.getLabel())) ? sg.getLabel() : g);
            // Wrap icon + label in a single span carrying the group's raw CSS style (like tags), so the
            // whole node (icon and text) is formatted together; jstree's own icon is disabled.
            final String content = "<span" + (css != null ? " style=\"" + escapeHtml(css) + "\"" : "") + ">"
                    + "<i class=\"" + escapeHtml(iconClass) + "\"></i> " + label + "</span>";
            final boolean opened = (sg != null) && !sg.isCollapsed();
            final org.json.JSONObject groupNode = new org.json.JSONObject();
            groupNode.put("id", "g:" + g);
            groupNode.put("text", content + " <span class=\"text-muted\">(" + params.size() + ")</span>");
            groupNode.put("icon", false);
            groupNode.put("children", children);
            groupNode.put("state", new org.json.JSONObject().put("opened", opened));
            root.put(groupNode);
        }
        return root.toString();
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max) + "…";
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    // --- Internals ---

    private GeneralCache region() {
        return cacheService.getCache(CACHE_REGION);
    }

    private Sysparam findRow(String key) {
        return inSession(() -> {
            final JPADAOListConfiguration cfg = sysparamDAO.build();
            cfg.withPredicate((cb, root) -> cb.equal(root.get("key"), key));
            return (Sysparam) sysparamDAO.find(cfg);
        });
    }

    /**
     * Runs a DB read either on the bound Hibernate session (request/transaction context) or,
     * when none is bound (a bean init-method, a background thread), on a short-lived session.
     * DAO.build() needs {@code getCurrentSession}, so this keeps sysparam reads working at
     * bootstrap and off-request time.
     */
    private <T> T inSession(java.util.function.Supplier<T> work) {
        if (org.springframework.transaction.support.TransactionSynchronizationManager.hasResource(sessionFactory)) {
            return work.get();
        }
        final Object[] holder = new Object[1];
        JPMUtils.executeInNewSessionNoTx(sessionFactory, (session, tx) -> holder[0] = work.get(), null);
        @SuppressWarnings("unchecked")
        final T result = (T) holder[0];
        return result;
    }

    private String decryptQuietly(String value) {
        try {
            return cipher.decrypt(value);
        } catch (PMException e) {
            JPMUtils.getLogger().error("No se pudo desencriptar un sysparam secreto", e);
            return null;
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
