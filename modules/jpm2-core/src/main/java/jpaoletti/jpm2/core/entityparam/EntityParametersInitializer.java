package jpaoletti.jpm2.core.entityparam;

import jpaoletti.jpm2.core.crypto.SysparamCipher;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Boot-time wiring that publishes the {@link EntityParameterCatalog} and the {@link SysparamCipher} into the
 * static {@link EntityParameters} facade, so entity-method resolution can decrypt secrets. Declare it as a
 * bean with {@code init-method="init"} (and {@code depends-on} the catalog/cipher beans). The cipher is
 * optional — where absent, secret parameters are treated as plaintext (passthrough).
 *
 * @author jpaoletti
 */
public class EntityParametersInitializer {

    @Autowired
    private EntityParameterCatalog catalog;

    @Autowired(required = false)
    private SysparamCipher cipher;

    public void init() {
        catalog.build();
        EntityParameters.configure(catalog, cipher);
        if (cipher == null || !cipher.isEnabled()) {
            JPMUtils.getLogger().warn("EntityParameters: no enabled SysparamCipher; secret parameters will be "
                    + "stored/read as plaintext until a cipher is configured.");
        }
    }

    public void setCatalog(EntityParameterCatalog catalog) {
        this.catalog = catalog;
    }

    public void setCipher(SysparamCipher cipher) {
        this.cipher = cipher;
    }
}
