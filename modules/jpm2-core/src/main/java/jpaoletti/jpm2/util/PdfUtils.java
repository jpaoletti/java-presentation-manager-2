package jpaoletti.jpm2.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * Helpers to build a table-shaped PDF from an XSL-FO document generated on the
 * fly (columns/headers/rows come from the entity field configuration) and to
 * render it with Apache FOP. Counterpart of {@link XlsUtils} for PDF output.
 *
 * @author jpaoletti
 */
public class PdfUtils {

    public static final String MIME_PDF = "application/pdf";
    public static final String PORTRAIT = "portrait";
    public static final String LANDSCAPE = "landscape";

    private static final FopFactory FOP_FACTORY = FopFactory.newInstance(new File(".").toURI());

    /**
     * A page size defined by its width and height in millimeters (portrait).
     */
    public enum PageSize {

        A3(297, 420), A4(210, 297), A5(148, 210), LETTER(216, 279), LEGAL(216, 356);

        private final int widthMm;
        private final int heightMm;

        PageSize(int widthMm, int heightMm) {
            this.widthMm = widthMm;
            this.heightMm = heightMm;
        }

        public static PageSize from(String name) {
            if (name != null) {
                for (PageSize s : values()) {
                    if (s.name().equalsIgnoreCase(name.trim())) {
                        return s;
                    }
                }
            }
            return A4;
        }
    }

    /**
     * Build a XSL-FO document rendering a single table with a title.
     *
     * @param title the document/table title
     * @param headers the column headers (already localized)
     * @param rows the row values, each row a list aligned with headers
     * @param alignments per-column text alignment (left/right/center); aligned
     * with headers, may be null for all-left
     * @param landscape true for landscape orientation, false for portrait
     * @param pageSize the page size name (A4, A3, Letter...); defaults to A4
     * @return the XSL-FO document as a String
     */
    public static String buildFo(String title, List<String> headers, List<List<String>> rows, List<String> alignments, boolean landscape, String pageSize) {
        final PageSize size = PageSize.from(pageSize);
        final int w = landscape ? size.heightMm : size.widthMm;
        final int h = landscape ? size.widthMm : size.heightMm;
        final int columns = headers.size();
        final StringBuilder fo = new StringBuilder(1024 + rows.size() * 64 * Math.max(1, columns));
        fo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fo.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n");
        fo.append("  <fo:layout-master-set>\n");
        fo.append("    <fo:simple-page-master master-name=\"page\"")
                .append(" page-width=\"").append(w).append("mm\"")
                .append(" page-height=\"").append(h).append("mm\"")
                .append(" margin=\"12mm\">\n");
        fo.append("      <fo:region-body margin-bottom=\"12mm\"/>\n");
        fo.append("      <fo:region-after extent=\"10mm\"/>\n");
        fo.append("    </fo:simple-page-master>\n");
        fo.append("  </fo:layout-master-set>\n");
        fo.append("  <fo:page-sequence master-reference=\"page\">\n");
        // Page footer with page number
        fo.append("    <fo:static-content flow-name=\"xsl-region-after\">\n");
        fo.append("      <fo:block font-size=\"8pt\" text-align=\"end\" color=\"#666666\">")
                .append("<fo:page-number/> / <fo:page-number-citation ref-id=\"last-block\"/></fo:block>\n");
        fo.append("    </fo:static-content>\n");
        fo.append("    <fo:flow flow-name=\"xsl-region-body\">\n");
        // Title
        fo.append("      <fo:block font-size=\"14pt\" font-weight=\"bold\" space-after=\"6mm\">")
                .append(escape(title)).append("</fo:block>\n");
        // Table
        fo.append("      <fo:table table-layout=\"fixed\" width=\"100%\" border=\"0.5pt solid #999999\">\n");
        for (int c = 0; c < columns; c++) {
            fo.append("        <fo:table-column column-width=\"proportional-column-width(1)\"/>\n");
        }
        // Header
        fo.append("        <fo:table-header>\n");
        fo.append("          <fo:table-row>\n");
        for (int c = 0; c < columns; c++) {
            fo.append("            <fo:table-cell background-color=\"#e9e9e9\" border=\"0.5pt solid #999999\" padding=\"2pt\">\n");
            fo.append("              <fo:block font-size=\"8pt\" font-weight=\"bold\" text-align=\"").append(align(alignments, c)).append("\">")
                    .append(escape(headers.get(c))).append("</fo:block>\n");
            fo.append("            </fo:table-cell>\n");
        }
        fo.append("          </fo:table-row>\n");
        fo.append("        </fo:table-header>\n");
        // Body
        fo.append("        <fo:table-body>\n");
        for (List<String> row : rows) {
            fo.append("          <fo:table-row>\n");
            for (int c = 0; c < columns; c++) {
                final String value = c < row.size() ? row.get(c) : "";
                fo.append("            <fo:table-cell border=\"0.5pt solid #cccccc\" padding=\"2pt\">\n");
                fo.append("              <fo:block font-size=\"8pt\" text-align=\"").append(align(alignments, c)).append("\">")
                        .append(escape(value)).append("</fo:block>\n");
                fo.append("            </fo:table-cell>\n");
            }
            fo.append("          </fo:table-row>\n");
        }
        fo.append("        </fo:table-body>\n");
        fo.append("      </fo:table>\n");
        // Anchor for the "last page" citation
        fo.append("      <fo:block id=\"last-block\"/>\n");
        fo.append("    </fo:flow>\n");
        fo.append("  </fo:page-sequence>\n");
        fo.append("</fo:root>\n");
        return fo.toString();
    }

    /**
     * Render a XSL-FO document to PDF bytes using Apache FOP.
     *
     * @param fo the XSL-FO document
     * @return the generated PDF as a byte array
     * @throws Exception on transformation/rendering errors
     */
    public static byte[] foToPdf(String fo) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Fop fop = FOP_FACTORY.newFop(MimeConstants.MIME_PDF, out);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer(); // identity
        transformer.transform(new StreamSource(new StringReader(fo)), new SAXResult(fop.getDefaultHandler()));
        return out.toByteArray();
    }

    /**
     * Normalize a per-column alignment to a valid XSL-FO {@code text-align}
     * value, defaulting to "left".
     */
    private static String align(List<String> alignments, int column) {
        if (alignments != null && column < alignments.size()) {
            final String a = alignments.get(column);
            if ("right".equalsIgnoreCase(a) || "center".equalsIgnoreCase(a) || "left".equalsIgnoreCase(a)) {
                return a.toLowerCase();
            }
        }
        return "left";
    }

    /**
     * Reduce a converted display value to plain text: many JPM2 web converters
     * wrap their output in HTML (e.g. {@code <span class='to-string' ...>value</span>}),
     * which would otherwise show up as literal markup in the PDF. This removes
     * tags and decodes a few common entities so the cell shows just the value.
     * Accent entities (&amp;aacute; ...) are handled later by {@link #escape(String)}.
     */
    public static String stripHtml(String s) {
        if (s == null) {
            return "";
        }
        String r = s.replaceAll("<[^>]*>", "");
        r = r.replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
        return r.trim();
    }

    /**
     * Escape a text value for safe inclusion in the XSL-FO (XML) document.
     */
    public static String escape(String s) {
        if (s == null) {
            return "";
        }
        final String clean = XlsUtils.replaceHtmlCodeAccents(s);
        final StringBuilder sb = new StringBuilder(clean.length());
        for (int i = 0; i < clean.length(); i++) {
            final char ch = clean.charAt(i);
            switch (ch) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }
}
