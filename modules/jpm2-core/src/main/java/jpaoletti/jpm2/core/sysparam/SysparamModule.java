package jpaoletti.jpm2.core.sysparam;

import java.util.Collections;
import java.util.List;

/**
 * An application-provided contribution to the sysparam catalog. Each app declares one or
 * more {@code SysparamModule} beans exposing its managed parameter definitions and dynamic
 * families; the {@link SysparamCatalog} collects them all at startup to seed defaults and
 * resolve typed access.
 *
 * @author jpaoletti
 */
public interface SysparamModule {

    /**
     * @return the managed parameter definitions this module contributes.
     */
    default List<SysparamDef<?>> params() {
        return Collections.emptyList();
    }

    /**
     * @return the dynamic families (open key prefixes) this module contributes.
     */
    default List<SysparamFamily> families() {
        return Collections.emptyList();
    }
}
