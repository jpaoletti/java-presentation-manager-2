package jpaoletti.jpm2.web.controller;

import java.io.ByteArrayInputStream;
import jpaoletti.jpm2.core.model.Entity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.mock.web.MockHttpServletResponse;

public class ListControllerExcelTest {

    @Test
    public void downloadsXlsxWithMatchingHeaders() throws Exception {
        verifyDownload(new SXSSFWorkbook(), "xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Test
    public void downloadsXlsWithMatchingHeaders() throws Exception {
        verifyDownload(new HSSFWorkbook(), "xls", "application/vnd.ms-excel");
    }

    private void verifyDownload(Workbook workbook, String extension, String contentType) throws Exception {
        final Entity entity = new Entity() {
            @Override
            public String getPluralTitle() {
                return "Árboles";
            }
        };
        workbook.createSheet("Items").createRow(0).createCell(0).setCellValue("Test");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        new ListController().writeExcelResponse(entity, workbook, response);
        assertEquals(contentType, response.getContentType());
        assertEquals("attachment; filename=\"Arboles." + extension + "\"", response.getHeader("Content-Disposition"));
        try (Workbook reopened = WorkbookFactory.create(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            assertEquals(workbook instanceof HSSFWorkbook ? HSSFWorkbook.class : XSSFWorkbook.class, reopened.getClass());
            assertEquals("Test", reopened.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        }
    }
}
