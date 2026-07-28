package jpaoletti.jpm2.core.sysparam;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SysparamDef} typed defaults and constraint validation.
 *
 * @author jpaoletti
 */
public class SysparamDefTest {

    @Test
    public void typedDefaultAndParse() {
        final SysparamDef<Integer> def = SysparamDef.integer("x").def(10).build();
        assertEquals(10, def.getDefault());
        assertEquals("10", def.getDefaultRaw());
        assertEquals(42, def.parse("42"));
    }

    @Test
    public void rangeValidation() {
        final SysparamDef<Integer> def = SysparamDef.integer("port").def(25).range(1, 65535).build();
        assertNull(def.validate("8080"));
        assertNotNull(def.validate("0"));
        assertNotNull(def.validate("70000"));
        assertNotNull(def.validate("abc"));
    }

    @Test
    public void requiredValidation() {
        final SysparamDef<String> def = SysparamDef.string("name").required().build();
        assertNotNull(def.validate(""));
        assertNotNull(def.validate(null));
        assertNull(def.validate("something"));
    }

    @Test
    public void allowedValuesValidation() {
        final SysparamDef<String> def = SysparamDef.enumOf("mode", "STD", "FAST").def("STD").build();
        assertNull(def.validate("STD"));
        assertNull(def.validate("FAST"));
        assertNotNull(def.validate("NOPE"));
    }

    @Test
    public void secretFactoryUsesSecretType() {
        final SysparamDef<String> def = SysparamDef.secret("api.key").build();
        assertEquals(SysparamType.SECRET, def.getType());
        // Secrecy is derived from the type, not a separate flag.
        assertTrue(def.isSecret());
    }

    @Test
    public void regexValidation() {
        final SysparamDef<String> def = SysparamDef.string("code").regex("[A-Z]{3}").build();
        assertNull(def.validate("ABC"));
        assertNotNull(def.validate("ab"));
    }
}
