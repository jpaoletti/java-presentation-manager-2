package jpaoletti.jpm2.core.entityparam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Auto-populates a {@link ParameterizedEntity}'s child parameters from its effective
 * {@link ParameterizedEntity#parameterCatalog() catalog}: for every declared parameter not already present it
 * creates a child (via {@link ParameterizedEntity#newParameter(String, String)}) with the catalog default as
 * its value (null for secrets, whose defaults are never seeded). Idempotent — existing parameters (by name,
 * case-insensitive) are left untouched.
 *
 * <p>Typical use is from an {@code OperationContext.preExecute} on the add operation
 * ({@link SeedEntityParametersOnAddContext}), so a freshly created owner comes pre-filled with the parameters
 * its selected implementation reads. It does NOT persist — the owner's cascade does, when the add saves it.
 *
 * @author jpaoletti
 */
public final class EntityParameterSeeder {

    private EntityParameterSeeder() {
    }

    /**
     * Adds the missing catalog parameters (with their defaults) to {@code owner.getParameters()}.
     *
     * @return the number of parameters added
     */
    public static <P extends EntityParameter> int seed(ParameterizedEntity<P> owner) {
        if (owner == null) {
            return 0;
        }
        final List<P> params = owner.getParameters();
        if (params == null) {
            return 0;
        }
        final Set<String> existing = new HashSet<>();
        for (P p : params) {
            if (p != null && p.getName() != null) {
                existing.add(p.getName().toLowerCase());
            }
        }
        int added = 0;
        for (EntityParameterDef<?> def : owner.parameterCatalog()) {
            if (def.getKey() == null || existing.contains(def.getKey().toLowerCase())) {
                continue;
            }
            final String value = def.isSecret() ? null : def.getDefaultRaw();
            final P param = owner.newParameter(def.getKey(), value);
            if (param != null) {
                params.add(param);
                existing.add(def.getKey().toLowerCase());
                added++;
            }
        }
        return added;
    }
}
