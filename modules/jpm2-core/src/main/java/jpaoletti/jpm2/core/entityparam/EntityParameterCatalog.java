package jpaoletti.jpm2.core.entityparam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * In-memory registry of the code-declared entity-parameter definitions, assembled from every
 * {@link EntityParameterModule} bean an application provides. Analogous to {@code SysparamCatalog} but the
 * keyspace is {@code (kind, key)} rather than a flat global key. It is the single source of truth for
 * defaults, types, validation and secrecy per parameterized entity.
 *
 * @author jpaoletti
 */
public class EntityParameterCatalog {

    @Autowired(required = false)
    private List<EntityParameterModule> modules = new ArrayList<>();

    /** Outer map keyed by kind; inner map keyed by parameter key. */
    private final Map<String, Map<String, EntityParameterDef<?>>> defs = new LinkedHashMap<>();
    private volatile boolean built = false;

    public synchronized void build() {
        if (built) {
            return;
        }
        defs.clear();
        if (modules != null) {
            for (EntityParameterModule module : modules) {
                for (EntityParameterDef<?> def : module.params()) {
                    defs.computeIfAbsent(def.getKind(), k -> new LinkedHashMap<>()).put(def.getKey(), def);
                }
            }
        }
        built = true;
    }

    public EntityParameterDef<?> defFor(String kind, String key) {
        build();
        final Map<String, EntityParameterDef<?>> byKey = defs.get(kind);
        return byKey == null ? null : byKey.get(key);
    }

    /** @return true when the (kind, key) is covered by a definition. */
    public boolean isManaged(String kind, String key) {
        return defFor(kind, key) != null;
    }

    /** @return true when the effective value should be encrypted at rest. */
    public boolean isSecret(String kind, String key) {
        final EntityParameterDef<?> def = defFor(kind, key);
        return def != null && def.isSecret();
    }

    /** @return the definitions declared for a kind (empty when none). */
    public Map<String, EntityParameterDef<?>> defsForKind(String kind) {
        build();
        final Map<String, EntityParameterDef<?>> byKey = defs.get(kind);
        return byKey == null ? new LinkedHashMap<>() : byKey;
    }

    public Map<String, Map<String, EntityParameterDef<?>>> allDefs() {
        build();
        return defs;
    }
}
