package jpaoletti.jpm2.web.sysparam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jpaoletti.jpm2.core.sysparam.SysparamDef;
import jpaoletti.jpm2.core.sysparam.SysparamFamily;
import jpaoletti.jpm2.core.sysparam.SysparamModule;
import jpaoletti.jpm2.core.sysparam.SysparamType;

/**
 * Demo/test sysparam catalog for the jpm2-web-bs5-test testbed: exercises typed access,
 * validation, a SECRET (encrypt/reveal) and a dynamic family. Declared as a bean in
 * {@code jpm-modules.xml} (no component scanning in this project).
 *
 * @author jpaoletti
 */
public class TestSysparamModule implements SysparamModule {

    public static final SysparamDef<Integer> DEMO_MAX_ITEMS
            = SysparamDef.integer("demo.max-items").def(10).group("demo").range(1, 1000).build();

    public static final SysparamDef<Boolean> DEMO_FEATURE_X
            = SysparamDef.bool("demo.feature-x").def(false).group("demo").build();

    public static final SysparamDef<String> DEMO_API_KEY
            = SysparamDef.secret("demo.api-key").group("demo").build();

    public static final SysparamDef<String> DEMO_MODE
            = SysparamDef.enumOf("demo.mode", "STD", "FAST", "SAFE").def("STD").group("demo").build();

    @Override
    public List<SysparamDef<?>> params() {
        final List<SysparamDef<?>> defs = new ArrayList<>();
        defs.add(DEMO_MAX_ITEMS);
        defs.add(DEMO_FEATURE_X);
        defs.add(DEMO_API_KEY);
        defs.add(DEMO_MODE);
        return defs;
    }

    @Override
    public List<SysparamFamily> families() {
        return Collections.singletonList(
                SysparamFamily.of("demo.dyn-").memberType(SysparamType.STRING).group("demo").build());
    }
}
