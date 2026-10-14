package jpaoletti.jpm2.core.menu;

import java.util.Collections;
import java.util.List;
import jpaoletti.jpm2.core.sysparam.SysparamDef;
import jpaoletti.jpm2.core.sysparam.SysparamModule;

/**
 * Sysparams of the dynamic menu. Declared in {@code entities/jpmMenu.xml}.
 *
 * @author jpaoletti
 */
public class JpmMenuSysparamModule implements SysparamModule {

    /** When true, the top menu is rendered from the jpm_menu table instead of topmenu.jsp. */
    public static final SysparamDef<Boolean> DYNAMIC
            = SysparamDef.bool("jpm.menu.dynamic").def(false).group("jpm").build();

    @Override
    public List<SysparamDef<?>> params() {
        return Collections.singletonList(DYNAMIC);
    }
}
