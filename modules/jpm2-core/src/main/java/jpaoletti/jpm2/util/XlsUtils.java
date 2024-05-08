package jpaoletti.jpm2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author jpaoletti
 */
public class XlsUtils {

    public static final String MIME_XLS = "application/vnd.ms-excel";
    public static SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static void xlsFormula(Row row, int pos, String formula, CellStyle style) {
        final Cell cell = row.createCell(pos);
        //cell.setCellType(CellType.FORMULA);
        cell.setCellFormula(formula);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    public static Workbook xlsStrechColumns(final Workbook wb) {
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            final Sheet s = wb.getSheetAt(i);
            for (int j = 0; j < 40; j++) { //mmm
                s.autoSizeColumn(j);
            }
        }
        return wb;
    }

    public static byte[] xlsTobytes(final Workbook wb) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        final byte[] res = out.toByteArray();
        return res;
    }

    public static Sheet xlsNewPage(final Workbook wb, XlsFormatTitle titleFormat) {
        final List<String> columntitles = titleFormat.getTitulosColumnas();
        if (wb.getSheet(titleFormat.getTitulo()) != null) {
            return xlsNewPage(wb, new XlsFormatTitle(
                    titleFormat.getTitulo() + ".",
                    titleFormat.getTituloInterno(),
                    titleFormat.getTitleColor(), columntitles));
        }
        final Sheet sheet = wb.createSheet(titleFormat.getTitulo());
        int r = titleFormat.getTituloInterno() != null ? xlsTitle(sheet, 0, titleFormat.getTituloInterno(), columntitles.size()) + 1 : 0;
        if (columntitles != null && !columntitles.isEmpty()) {
            sheet.createFreezePane(0, 3);
            xlsHeaderRow(wb, sheet, r, columntitles, titleFormat.getTitleColor());
        }
        return sheet;
    }

    public static void xlsHeaderRow(final Workbook wb, final Sheet sheet, int rownum, final List<String> columntitles, short color) {
        final CellStyle bold = xlsBoldStyle(wb);
        final Row headerRow = sheet.createRow(rownum);
        int i = 0;
        for (String string : columntitles) {
            final Cell cell = headerRow.createCell(i++);
            cell.setCellStyle(bold);
            cell.setCellValue(string);
            bold.setFillForegroundColor(color);
            bold.setFillPattern(SOLID_FOREGROUND);
            bold.setBorderBottom(BorderStyle.THIN);
            bold.setBorderLeft(BorderStyle.THIN);
            bold.setBorderRight(BorderStyle.THIN);
            bold.setBorderTop(BorderStyle.THIN);
        }
    }

    public static int xlsTitle(final Sheet sheet, int r, final String title, int colspan) {
        final Cell tmp = sheet.createRow(r++).createCell(0);
        tmp.setCellValue(String.format("%s. Generated: %s", title, DATETIME_FORMAT.format(new Date()))); //improve
        sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, colspan == 0 ? 5 : colspan));
        return r++;
    }

    public static CellStyle xlsBoldStyle(final Workbook wb) {
        final CellStyle cs = wb.createCellStyle();
        final Font f = wb.createFont();
        f.setBold(true);
        cs.setFont(f);
        return cs;
    }

    public static CellStyle xlsGrayBoldStyle(final Workbook wb) {
        final CellStyle cs = wb.createCellStyle();
        final Font f = wb.createFont();
        f.setBold(true);
        cs.setFont(f);
        cs.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cs;
    }

    public static CellStyle xlsAmountStyle(final Workbook wb) {
        final CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(wb.createDataFormat().getFormat("#,##0.00;-#,##0.00"));
        return cs;
    }

    public static CellStyle xlsDateStyle(final Workbook wb) {
        final CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(wb.createDataFormat().getFormat("DD/MM/YYYY"));
        return cs;
    }

    public static CellStyle xlsDateTimeStyle(final Workbook wb) {
        final CellStyle cs = wb.createCellStyle();
        cs.setDataFormat(wb.createDataFormat().getFormat("DD/MM/YYYY hh:mm:ss"));
        return cs;
    }

    public static Cell xlsCellWithStyle(Cell cell, CellStyle style) {
        cell.setCellStyle(style);
        return cell;
    }

    public static int xlsSubtitle(final Sheet sheet, int r, String v1, String v2, int cantidad, CellStyle style) {
        final Row row = sheet.createRow(r++);
        if (style != null) {
            xlsCellWithStyle(row.createCell(0), style).setCellValue(v1);
            xlsCellWithStyle(row.createCell(1), style).setCellValue(v2);
        } else {
            row.createCell(0).setCellValue(v1);
            row.createCell(1).setCellValue(v2);
        }
        sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 1, cantidad));
        return r;
    }

    public static String replaceHtmlCodeAccents(String s) {
        return s.replaceAll("&aacute;", "á")
                .replaceAll("&eacute;", "é")
                .replaceAll("&iacute;", "í")
                .replaceAll("&oacute;", "ó")
                .replaceAll("&uacute;", "ú")
                .replaceAll("&Aacute;", "Á")
                .replaceAll("&Eacute;", "É")
                .replaceAll("&Iacute;", "Í")
                .replaceAll("&Oacute;", "Ó")
                .replaceAll("&Uacute;", "Ú")
                .replaceAll("&Nacute;", "Ñ")
                .replaceAll("&ntilde;", "ñ");
    }

    public static String xlsGetCellValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case BOOLEAN:
                    return Boolean.toString(cell.getBooleanCellValue());
                case NUMERIC:
                    DataFormatter df = new DataFormatter();
                    return df.formatCellValue(cell);
//                    final NumberFormat formatter = new DecimalFormat("#0");
//                    return formatter.format(cell.getNumericCellValue());
                case STRING:
                    return cell.getStringCellValue();
            }
        }
        return "";
    }

    public static Cell xlsSetCellValue(Row row, int i, CellStyle style, String value) {
        final Cell cell = row.createCell(i);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }

    public static short color_yellow() {
        return HSSFColor.HSSFColorPredefined.YELLOW.getIndex();
    }

    public static class XlsFormatTitle {

        private final String titulo;
        private final String tituloInterno;
        private final short titleColor;
        private final List<String> titulosColumnas = new ArrayList<>();

        public XlsFormatTitle(String titulo, String tituloInterno, short titleColor, List<String> titulos) {
            this.titulo = titulo;
            this.titleColor = titleColor;
            this.tituloInterno = tituloInterno;
            this.titulosColumnas.addAll(titulos);
        }

        public XlsFormatTitle(String titulo, String tituloInterno, List<String> titulos) {
            this(titulo, tituloInterno, color_yellow(), titulos);
        }

        public XlsFormatTitle(String titulo, String tituloInterno, String... titulos) {
            this(titulo, tituloInterno, Arrays.asList(titulos));
        }

        public XlsFormatTitle(String titulo, String tituloInterno, short titleColor, String... titulos) {
            this(titulo, tituloInterno, titleColor, Arrays.asList(titulos));
        }

        public String getTitulo() {
            return titulo;
        }

        public String getTituloInterno() {
            return tituloInterno;
        }

        public short getTitleColor() {
            return titleColor;
        }

        public List<String> getTitulosColumnas() {
            return titulosColumnas;
        }
    }
}
