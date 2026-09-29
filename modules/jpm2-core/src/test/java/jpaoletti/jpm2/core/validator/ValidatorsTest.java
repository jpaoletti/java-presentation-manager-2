package jpaoletti.jpm2.core.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import jpaoletti.jpm2.core.model.PaginatedList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ValidatorsTest {

    @Test
    void dateMaxParsesTheDocumentedFormat() {
        final DateMax v = new DateMax();
        v.setMax("2025-12-31");
        assertNull(v.validate(null, date(2025, 6, 1)));
        assertNotNull(v.validate(null, date(2026, 1, 15)));
        assertNull(v.validate(null, null));
    }

    @Test
    void dateMaxWithoutMaxComparesWithNow() {
        final DateMax v = new DateMax();
        assertNull(v.validate(null, new Date(System.currentTimeMillis() - 60000)));
        assertNotNull(v.validate(null, new Date(System.currentTimeMillis() + 86400000L)));
    }

    @Test
    void dateMinParsesTheDocumentedFormat() {
        final DateMin v = new DateMin();
        v.setMin("2025-01-01");
        assertNull(v.validate(null, date(2025, 6, 1)));
        assertNotNull(v.validate(null, date(2024, 6, 1)));
    }

    @Test
    void bigDecimalLimits() {
        final BigDecimalMax max = new BigDecimalMax();
        assertNull(max.validate(null, new BigDecimal("1000")), "without max there is no limit");
        max.setMax(new BigDecimal("100"));
        assertNull(max.validate(null, new BigDecimal("100")));
        assertNotNull(max.validate(null, new BigDecimal("100.01")));
        final BigDecimalMin min = new BigDecimalMin();
        assertNull(min.validate(null, new BigDecimal("-5")), "without min there is no limit");
        min.setMin(BigDecimal.ZERO);
        assertNotNull(min.validate(null, new BigDecimal("-0.01")));
    }

    @Test
    void validMailAcceptsEmptyValues() {
        final ValidMail v = new ValidMail();
        assertNull(v.validate(null, null));
        assertNull(v.validate(null, ""));
        assertNull(v.validate(null, "someone@example.com"));
        assertNotNull(v.validate(null, "not a mail"));
    }

    @Test
    void notEmptySupportsStringsAndCollections() {
        final NotEmpty v = new NotEmpty();
        assertNotNull(v.validate(null, null));
        assertNotNull(v.validate(null, "  "));
        assertNotNull(v.validate(null, new ArrayList<>()));
        assertNull(v.validate(null, "x"));
        assertNull(v.validate(null, Arrays.asList(1)));
    }

    @Test
    void paginationIsNormalized() {
        final PaginatedList pl = new PaginatedList();
        pl.setPageSize(0);
        assertEquals(PaginatedList.DEFAULT_PAGE_SIZE, pl.getPageSize());
        pl.setPage(-3);
        assertEquals(1, pl.getPage());
        assertEquals(0, pl.from());
        pl.setTotal(0L);
        pl.setPage(5);
        assertEquals(1, pl.getPage());
        pl.setPageSize(Integer.MAX_VALUE);
        pl.setTotal(null);
        pl.setPage(3);
        assertEquals(Integer.MAX_VALUE, pl.from());
    }

    private static Date date(int y, int m, int d) {
        final Calendar c = Calendar.getInstance();
        c.clear();
        c.set(y, m - 1, d);
        return c.getTime();
    }
}
