package jpaoletti.jpm2.core.sysparam;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SysparamType} parsing and validation.
 *
 * @author jpaoletti
 */
public class SysparamTypeTest {

    @Test
    public void parsesScalars() {
        assertEquals(5, SysparamType.INTEGER.parse("5"));
        assertEquals(9999999999L, SysparamType.LONG.parse("9999999999"));
        assertEquals(new BigDecimal("12.34"), SysparamType.DECIMAL.parse("12.34"));
        assertEquals(Boolean.TRUE, SysparamType.BOOLEAN.parse("yes"));
        assertEquals(Boolean.FALSE, SysparamType.BOOLEAN.parse("off"));
    }

    @Test
    public void parsesList() {
        final Object parsed = SysparamType.LIST.parse("a, b ,c");
        assertTrue(parsed instanceof List);
        assertEquals(List.of("a", "b", "c"), parsed);
    }

    @Test
    public void validateRejectsMalformed() {
        assertNotNull(SysparamType.INTEGER.validate("abc"));
        assertNull(SysparamType.INTEGER.validate("42"));
        assertNotNull(SysparamType.URL.validate("not-a-url"));
        assertNull(SysparamType.URL.validate("https://example.com/x"));
        assertNotNull(SysparamType.EMAIL.validate("nope"));
        assertNull(SysparamType.EMAIL.validate("a@b.co"));
    }

    @Test
    public void blankIsValidForType() {
        // Emptiness is enforced by the 'required' flag on the definition, not by the type.
        assertNull(SysparamType.INTEGER.validate(""));
        assertNull(SysparamType.INTEGER.validate(null));
    }

    @Test
    public void nullParseReturnsNull() {
        assertNull(SysparamType.STRING.parse(null));
        assertNull(SysparamType.INTEGER.parse(null));
    }
}
