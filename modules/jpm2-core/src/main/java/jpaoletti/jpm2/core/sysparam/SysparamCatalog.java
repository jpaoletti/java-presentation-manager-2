package jpaoletti.jpm2.core.sysparam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * In-memory registry of the code-declared sysparam definitions and dynamic families,
 * assembled from every {@link SysparamModule} bean an application provides. It is the
 * single source of truth for defaults, types and validation; the DB only stores overrides.
 *
 * @author jpaoletti
 */
public class SysparamCatalog {

    @Autowired(required = false)
    private List<SysparamModule> modules = new ArrayList<>();

    private final Map<String, SysparamDef<?>> defs = new LinkedHashMap<>();
    private final List<SysparamFamily> families = new ArrayList<>();
    private volatile boolean built = false;

    public synchronized void build() {
        if (built) {
            return;
        }
        defs.clear();
        families.clear();
        if (modules != null) {
            for (SysparamModule module : modules) {
                for (SysparamDef<?> def : module.params()) {
                    defs.put(def.getKey(), def);
                }
                families.addAll(module.families());
            }
        }
        built = true;
    }

    public SysparamDef<?> defFor(String key) {
        build();
        return defs.get(key);
    }

    public SysparamFamily familyFor(String key) {
        build();
        for (SysparamFamily family : families) {
            if (family.matches(key)) {
                return family;
            }
        }
        return null;
    }

    /** @return true when the key is covered by a definition or a dynamic family. */
    public boolean isManaged(String key) {
        return defFor(key) != null || familyFor(key) != null;
    }

    /** @return true when the effective value should be encrypted at rest. */
    public boolean isSecret(String key) {
        final SysparamDef<?> def = defFor(key);
        if (def != null) {
            return def.isSecret();
        }
        final SysparamFamily family = familyFor(key);
        return family != null && family.isSecret();
    }

    /** @return true when the value may be cached (secrets are never cached). */
    public boolean isCacheable(String key) {
        if (isSecret(key)) {
            return false;
        }
        final SysparamDef<?> def = defFor(key);
        if (def != null) {
            return def.isCached();
        }
        final SysparamFamily family = familyFor(key);
        return family == null || family.isCached();
    }

    public Map<String, SysparamDef<?>> allDefs() {
        build();
        return defs;
    }

    public List<SysparamFamily> allFamilies() {
        build();
        return families;
    }
}
