package jpaoletti.jpm2.core.entityparam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.crypto.SysparamCipher;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Shared resolution engine for {@link ParameterizedEntity} — the single implementation of the
 * {@code getParameter(name)} / typed getters / {@code getParameterMap()} that every parent used to copy-paste.
 * It reads the catalog + cipher from the {@link EntityParameters} facade, so it works from plain entity
 * methods with no Spring wiring:
 *
 * <ul>
 *   <li>single-name lookup honors {@code test-<name>} when {@link ParameterizedEntity#isTestMode()};</li>
 *   <li>values whose {@code (kind, name)} is secret in the catalog are transparently decrypted (the cipher's
 *       {@code decrypt} is a plaintext passthrough, so plain and encrypted values coexist during migration);</li>
 *   <li>an absent parameter falls back to the catalog default, then to the caller-supplied default.</li>
 * </ul>
 *
 * @author jpaoletti
 */
public final class EntityParameterResolver {

    private EntityParameterResolver() {
    }

    /** Raw (decrypted if secret) value for {@code name}, honoring test-mode override and catalog default. */
    public static String raw(ParameterizedEntity<?> owner, String name) {
        if (owner == null || name == null) {
            return null;
        }
        final EntityParameter param = find(owner, name);
        if (param != null) {
            return decryptIfSecret(owner, name, param.getValue());
        }
        final EntityParameterDef<?> def = defFor(owner, name);
        return def != null ? def.getDefaultRaw() : null;
    }

    /**
     * The definition governing {@code (owner, name)}: the owner's instance-scoped {@link
     * ParameterizedEntity#parameterCatalog()} wins, then the global {@link EntityParameterCatalog} for the
     * owner's kind. Returns null when no definition covers it (the parameter is then a plain string).
     *
     * <p><b>Matching is by EXACT key only.</b> A parameter whose key is built dynamically at runtime (e.g.
     * {@code getParameter("connector-" + nii)} → {@code connector-3}) still resolves its VALUE correctly —
     * that comes from the stored row by name, not from the catalog — but has no def, so it is treated as an
     * undeclared plaintext STRING: no typing, no catalog default, and (critically) it is never encrypted even
     * if its family were conceptually secret. Prefix/"family" matching (a def for {@code connector-} applying
     * to {@code connector-*}) is a deliberate future enhancement: it would only need to be added HERE, since
     * every read/write/UI path routes its def lookup through this method.
     */
    public static EntityParameterDef<?> defFor(ParameterizedEntity<?> owner, String name) {
        if (owner == null || name == null) {
            return null;
        }
        for (EntityParameterDef<?> def : owner.parameterCatalog()) {
            if (name.equalsIgnoreCase(def.getKey())) {
                return def;
            }
        }
        final EntityParameterCatalog catalog = EntityParameters.catalog();
        return catalog != null ? catalog.defFor(owner.getParameterKind(), name) : null;
    }

    /** @return true when {@code (owner, name)} resolves to a secret definition (encrypted at rest). */
    public static boolean isSecret(ParameterizedEntity<?> owner, String name) {
        final EntityParameterDef<?> def = defFor(owner, name);
        return def != null && def.isSecret();
    }

    public static String get(ParameterizedEntity<?> owner, String name, String def) {
        final String value = raw(owner, name);
        return value != null ? value : def;
    }

    public static Integer get(ParameterizedEntity<?> owner, String name, Integer def) {
        final String value = raw(owner, name);
        if (StringUtils.isBlank(value)) {
            return def;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static Long get(ParameterizedEntity<?> owner, String name, Long def) {
        final String value = raw(owner, name);
        if (StringUtils.isBlank(value)) {
            return def;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static boolean get(ParameterizedEntity<?> owner, String name, boolean def) {
        final String value = raw(owner, name);
        return StringUtils.isBlank(value) ? def : Boolean.parseBoolean(value.trim());
    }

    public static List<String> get(ParameterizedEntity<?> owner, String name, List<String> def) {
        final String value = raw(owner, name);
        if (StringUtils.isBlank(value)) {
            return def;
        }
        final List<String> result = new ArrayList<>();
        for (String part : value.split(",")) {
            final String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * All parameters as a name to (decrypted) value map. Unlike single-name lookup this does NOT apply the
     * test-mode override — it dumps every child by its literal name, decrypting the secret ones — preserving
     * the legacy {@code getParameterMap()} semantics.
     */
    public static Map<String, String> map(ParameterizedEntity<?> owner) {
        final Map<String, String> result = new HashMap<>();
        if (owner == null || owner.getParameters() == null) {
            return result;
        }
        for (EntityParameter param : owner.getParameters()) {
            if (param.getName() != null) {
                result.put(param.getName(), decryptIfSecret(owner, param.getName(), param.getValue()));
            }
        }
        return result;
    }

    // ---- internals --------------------------------------------------------

    private static EntityParameter find(ParameterizedEntity<?> owner, String name) {
        final List<? extends EntityParameter> params = owner.getParameters();
        if (params == null) {
            return null;
        }
        if (owner.isTestMode()) {
            final String testName = "test-" + name;
            for (EntityParameter param : params) {
                if (testName.equalsIgnoreCase(param.getName())) {
                    return param;
                }
            }
        }
        for (EntityParameter param : params) {
            if (name.equalsIgnoreCase(param.getName())) {
                return param;
            }
        }
        return null;
    }

    private static String decryptIfSecret(ParameterizedEntity<?> owner, String name, String value) {
        if (value == null) {
            return null;
        }
        final SysparamCipher cipher = EntityParameters.cipher();
        if (cipher != null && isSecret(owner, name)) {
            try {
                return cipher.decrypt(value);
            } catch (PMException e) {
                JPMUtils.getLogger().warn("Could not decrypt secret parameter '"
                        + owner.getParameterKind() + "/" + name + "'", e);
            }
        }
        return value;
    }
}
