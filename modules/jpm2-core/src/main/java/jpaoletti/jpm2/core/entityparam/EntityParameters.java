package jpaoletti.jpm2.core.entityparam;

import jpaoletti.jpm2.core.crypto.SysparamCipher;

/**
 * Static access point holding the active {@link EntityParameterCatalog} and {@link SysparamCipher}, so entity
 * methods (which have no Spring context) can resolve typed/secret parameters through
 * {@link EntityParameterResolver} without per-call injection. Modeled on the {@code DebugLog} static facility.
 *
 * <p>Configured once at boot by {@link EntityParametersInitializer}. Until configured it is inert: the
 * resolver then behaves as a plain name/value lookup with no decryption (so non-migrated consumers and unit
 * tests are unaffected). Tests can call {@link #configure(EntityParameterCatalog, SysparamCipher)} directly.
 *
 * @author jpaoletti
 */
public final class EntityParameters {

    private static volatile EntityParameterCatalog catalog;
    private static volatile SysparamCipher cipher;

    private EntityParameters() {
    }

    public static void configure(EntityParameterCatalog catalog, SysparamCipher cipher) {
        EntityParameters.catalog = catalog;
        EntityParameters.cipher = cipher;
    }

    public static EntityParameterCatalog catalog() {
        return catalog;
    }

    public static SysparamCipher cipher() {
        return cipher;
    }
}
