package jpaoletti.jpm2.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JPMServiceExcelTest {

    private Workbook export(String format, int rows) throws Exception {
        final Operation operation = new Operation("toExcel");
        if (format != null) {
            final Properties properties = new Properties();
            properties.setProperty("format", format);
            operation.setProperties(properties);
        }
        final JPMContext context = mock(JPMContext.class);
        when(context.getOperation()).thenReturn(operation);
        final PresentationManager jpm = mock(PresentationManager.class);
        final JPMService delegate = mock(JPMService.class);
        when(jpm.getService()).thenReturn(delegate);
        when(jpm.getMessage(anyString(), any(Object[].class))).thenReturn("Export");
        final Entity entity = mock(Entity.class);
        when(entity.getPluralTitle()).thenReturn("Items");
        final EntityInstance instance = mock(EntityInstance.class);
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put("text", "Árbol");
        values.put("amount", new BigDecimal("12.50"));
        values.put("active", true);
        values.put("empty", null);
        when(instance.getValues()).thenReturn(values);
        final EntityInstanceList contents = new EntityInstanceList();
        for (int i = 0; i < rows; i++) {
            contents.add(instance);
        }
        final PaginatedList list = mock(PaginatedList.class);
        when(list.getContents()).thenReturn(contents);
        final Field field = mock(Field.class);
        when(field.getTitle(entity)).thenReturn("Text");
        when(list.getFields()).thenReturn(List.of(field));
        final SessionEntityData sed = new SessionEntityData(entity, null);
        sed.setPage(2);
        sed.setPageSize(20);
        when(delegate.getPaginatedList(isNull(), eq(operation), eq(sed), eq(1), eq(Integer.MAX_VALUE), isNull(), isNull())).thenReturn(list);
        final JPMServiceImpl service = new JPMServiceImpl();
        service.setContext(context);
        service.setJpm(jpm);
        final Workbook workbook = service.toExcel(entity, sed, null, null);
        assertEquals(2, sed.getPage());
        assertEquals(20, sed.getPageSize());
        return workbook;
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"xlsx", "XLSX", "xls", "XLS"})
    void exportsSelectedFormatAndPreservesCellTypes(String format) throws Exception {
        final boolean legacy = format != null && format.equalsIgnoreCase("xls");
        final Workbook workbook = export(format, 1);
        try (workbook) {
            assertTrue(legacy ? workbook instanceof HSSFWorkbook : workbook instanceof SXSSFWorkbook);
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            workbook.write(bytes);
            try (Workbook reopened = WorkbookFactory.create(new ByteArrayInputStream(bytes.toByteArray()))) {
                final var row = reopened.getSheetAt(0).getRow(3);
                assertEquals("Árbol", row.getCell(0).getStringCellValue());
                assertEquals(12.5, row.getCell(1).getNumericCellValue());
                assertTrue(row.getCell(2).getBooleanCellValue());
                assertEquals("", row.getCell(3).getStringCellValue());
                assertEquals("Text", reopened.getSheetAt(0).getRow(2).getCell(0).getStringCellValue());
            }
        } finally {
            dispose(workbook);
        }
    }

    private static void dispose(Workbook workbook) {
        if (workbook instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) workbook).dispose();
        }
    }

    @Test
    void xlsxKeepsOnlyAWindowOfRowsInMemory() throws Exception {
        final Workbook workbook = export("xlsx", 1000);
        try (workbook) {
            // Flushed rows are no longer available in memory
            assertNull(workbook.getSheetAt(0).getRow(3));
            assertNotNull(workbook.getSheetAt(0).getRow(1002));
        } finally {
            dispose(workbook);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"xls", "xlsx"})
    void splitsSheetsAccordingToFormatRowLimit(String format) throws Exception {
        final Workbook workbook = export(format, 65534);
        try (workbook) {
            assertEquals(format.equals("xls") ? 2 : 1, workbook.getNumberOfSheets());
            assertEquals(format.equals("xls") ? 65535 : 65536, workbook.getSheetAt(0).getLastRowNum());
            if (format.equals("xls")) {
                assertNotNull(workbook.getSheetAt(1).getRow(3));
                assertNotNull(workbook.getSheetAt(1).getRow(2));
            }
        } finally {
            dispose(workbook);
        }
    }

    @Test
    void rejectsUnsupportedFormat() {
        assertThrows(IllegalArgumentException.class, () -> export("csv", 1));
    }
}
