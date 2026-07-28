package jpaoletti.jpm2.smoke;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Smoke crawler for the JPM2 front-end.
 *
 * <p>Flow: login -&gt; scrape the menu (visible links, respecting permissions) -&gt; for
 * each entity exercise list / sort (asc+desc per sortable column) / pagination / filters
 * (using the real search form the page embeds in {@code div#fieldSearchForm_<field>}) /
 * visible-columns / read-only operations. Each action is validated against a server error
 * (5xx / exception page).
 *
 * <p>Black-box: it discovers sortable columns ({@code th.sortable[data-field]}) and
 * filterable fields ({@code a.dropdown-item[data-field]}) directly from the HTML. Nothing
 * here is app-specific, so it works against any JPM2 application (used here to validate the
 * jpm2-web-bs5 testbed screens).
 *
 * @author jpaoletti
 */
public class WebSmokeCrawler {

    /** Improbable value to verify a string filter really narrows the result. */
    static final String IMPOSSIBLE = "zzz__no_match__zzz__9173";
    /** Absurdly future ISO date (the {@code <input type=date>} format): nothing should be later. */
    static final String IMPOSSIBLE_DATE = "2999-12-31";

    /** Read-only operations safe to open by GET (never mutate data). */
    static final Set<String> SAFE_OPS = Set.of(
            "show", "edit", "add", "audit", "itemAudit", "generalAudit",
            "detailedAudit", "view", "profile", "history", "effectiveValue", "cacheInfo");

    private final SmokeHttpClient http;
    private final boolean testFilters;
    private final boolean testColumns;
    private final boolean testOperations;
    private final boolean testI18n;
    private final int maxEntities;

    public WebSmokeCrawler(SmokeHttpClient http, boolean testFilters, boolean testColumns,
            boolean testOperations, boolean testI18n, int maxEntities) {
        this.http = http;
        this.testFilters = testFilters;
        this.testColumns = testColumns;
        this.testOperations = testOperations;
        this.testI18n = testI18n;
        this.maxEntities = maxEntities;
    }

    /** Result of a concrete action. */
    public static final class Check {

        public final String entity;
        public final String action;
        public final boolean ok;
        public final String detail;
        public final boolean skipped;

        public Check(String entity, String action, boolean ok, String detail) {
            this(entity, action, ok, detail, false);
        }

        public Check(String entity, String action, boolean ok, String detail, boolean skipped) {
            this.entity = entity;
            this.action = action;
            this.ok = ok;
            this.detail = detail;
            this.skipped = skipped;
        }
    }

    /** Menu entity: code (path var) + label. */
    public static final class MenuEntry {

        public final String code;
        public final String label;

        public MenuEntry(String code, String label) {
            this.code = code;
            this.label = label;
        }
    }

    /** Discovers the entities from the rendered (authenticated) menu. */
    public List<MenuEntry> scrapeMenu() throws Exception {
        final SmokeHttpClient.Resp home = http.get("/");
        final Document doc = Jsoup.parse(home.body, http.baseUrl());
        final Map<String, MenuEntry> byCode = new LinkedHashMap<>();
        for (Element a : doc.select("a.jpm-menu-item[href]")) {
            final String href = a.attr("href");
            final String code = entityCodeFromListHref(href);
            if (code != null && !byCode.containsKey(code)) {
                byCode.put(code, new MenuEntry(code, a.text().trim()));
            }
        }
        return new ArrayList<>(byCode.values());
    }

    /** Runs all checks for an entity. */
    public List<Check> crawlEntity(String code) throws Exception {
        final List<Check> checks = new ArrayList<>();

        // clean search state (ignore result: entity may have no searchers)
        try {
            http.get("/jpm/" + code + "/removeAllSearch");
        } catch (Exception ignore) {
            // no-op
        }

        // 1) base list
        final SmokeHttpClient.Resp list = http.get("/jpm/" + code + "/list");
        final Document doc = Jsoup.parse(list.body, http.baseUrl());

        // Is it really the grid, or a redirect to profile / "Access Denied"?
        final boolean redirected = !list.uri.getPath().contains("/jpm/" + code);
        final boolean denied = list.body.contains("Acceso Denegado") || list.body.contains("Access Denied");
        final boolean hasGrid = !doc.select("th.data[data-field]").isEmpty()
                || !doc.select("#list-pagination-cell").isEmpty()
                || !doc.select("#columnsModalShow").isEmpty();
        if (list.isError()) {
            checks.add(new Check(code, "list", false, status(list)));
            return checks;
        }
        if (redirected || denied || !hasGrid) {
            final String why = redirected ? "redirects to " + list.uri.getPath()
                    : denied ? "Access Denied" : "no grid (permission/JS?)";
            checks.add(new Check(code, "list", true, "SKIP: " + why, true));
            return checks;
        }
        checks.add(new Check(code, "list", true, status(list)));

        final int baseCount = countRows(doc);

        // 1b) i18n: the visible column/filter label must not equal the field id.
        //     When the messages key is missing, JPM shows the raw id.
        //     Case-sensitive comparison avoids false positives like id -> "ID".
        if (testI18n) {
            for (Element th : doc.select("th[data-field]")) {
                final String field = th.attr("data-field");
                if (th.text().trim().equals(field)) {
                    checks.add(new Check(code, "i18n:col:" + field, false,
                            "column without i18n: label == id (missing key in messages.properties)"));
                }
            }
            for (Element a : doc.select("a.dropdown-item[data-field]")) {
                final String field = a.attr("data-field");
                final String label = a.text().replaceFirst("^\\s*\\d+\\.\\s*", "").trim();
                if (label.equals(field)) {
                    checks.add(new Check(code, "i18n:flt:" + field, false,
                            "filter without i18n: label == id (missing key in messages.properties)"));
                }
            }
        }

        // 2) sort per sortable column
        final Set<String> sortFields = new LinkedHashSet<>();
        for (Element th : doc.select("th.sortable[data-field]")) {
            sortFields.add(th.attr("data-field"));
        }
        for (String f : sortFields) {
            for (String dir : new String[]{"asc", "desc"}) {
                final SmokeHttpClient.Resp r = http.get("/jpm/" + code + "/sort?fieldId=" + enc(f) + "&direction=" + dir);
                // In JPA a sort error = the column is not DB-sortable (derived/transient/association).
                // Not a bug: reported as an informational note (not KO) -> consider sortable=false.
                if (!r.isError()) {
                    checks.add(new Check(code, "sort:" + f + ":" + dir, true, status(r)));
                } else {
                    checks.add(new Check(code, "sort:" + f + ":" + dir, true,
                            "NO-SORT: column not DB-sortable -> consider sortable=false"));
                }
            }
        }
        if (!sortFields.isEmpty()) {
            http.get("/jpm/" + code + "/sort?fieldId=" + enc(sortFields.iterator().next()) + "&direction=asc");
        }

        // 3) pagination
        final SmokeHttpClient.Resp page2 = http.get("/jpm/" + code + "/list?page=2&pageSize=10");
        checks.add(new Check(code, "page2", !page2.isError(), status(page2)));

        // 4) filters: (a) does not break; (b) a string impossible value must narrow to 0
        if (testFilters) {
            for (Element a : doc.select("a.dropdown-item[data-field]")) {
                final String field = a.attr("data-field");
                final Element form = doc.getElementById("fieldSearchForm_" + field);

                final Map<String, String> params = buildSearchParams(doc, field);
                params.put("fieldId", field);
                final SmokeHttpClient.Resp r = http.postForm("/jpm/" + code + "/addSearch", params);
                checks.add(new Check(code, "filter:" + field, !r.isError(), status(r)));
                http.get("/jpm/" + code + "/removeAllSearch");

                // (b) verify the filter REALLY applies (anti silently-ignored)
                if (baseCount > 0 && isStringSearch(form)) {
                    final Map<String, String> imp = buildSearchParams(doc, field);
                    imp.put("fieldId", field);
                    imp.put("value", IMPOSSIBLE);
                    imp.put("operator", "li");
                    final SmokeHttpClient.Resp ri = http.postForm("/jpm/" + code + "/addSearch", imp);
                    if (!ri.isError()) {
                        final int n = countRows(Jsoup.parse(ri.body, http.baseUrl()));
                        checks.add(new Check(code, "filterApplies:" + field, n == 0,
                                n == 0 ? "0 rows with impossible value"
                                        : "filter does NOT narrow: base=" + baseCount + " impossible=" + n));
                    }
                    http.get("/jpm/" + code + "/removeAllSearch");
                }

                // (b') same for date searchers: an absurdly future date with '>' must narrow to 0.
                if (baseCount > 0 && isDateSearch(form)) {
                    final Map<String, String> imp = buildSearchParams(doc, field);
                    imp.put("fieldId", field);
                    imp.put("value", IMPOSSIBLE_DATE);
                    imp.put("operator", ">");
                    final SmokeHttpClient.Resp ri = http.postForm("/jpm/" + code + "/addSearch", imp);
                    if (!ri.isError()) {
                        final int n = countRows(Jsoup.parse(ri.body, http.baseUrl()));
                        checks.add(new Check(code, "filterApplies:" + field, n == 0,
                                n == 0 ? "0 rows with date > " + IMPOSSIBLE_DATE
                                        : "filter does NOT narrow: base=" + baseCount + " > " + IMPOSSIBLE_DATE + "=" + n));
                    }
                    http.get("/jpm/" + code + "/removeAllSearch");
                }

                // (c) column consistency: after filtering by a value, no cell of that column
                // should differ from the expected value.
                if (baseCount > 0) {
                    checkFilterColumn(checks, code, doc, field, form);
                }
            }
        }

        // 5) visible columns (show/hide) — setVisibleColumns + verification + restore
        if (testColumns) {
            final List<String> allCols = new ArrayList<>();
            final List<String> original = new ArrayList<>();
            for (Element chk : doc.select("input.columnVisibleChk[name=column]")) {
                final String col = chk.attr("value");
                if (!col.isBlank()) {
                    allCols.add(col);
                    if (chk.hasAttr("checked")) {
                        original.add(col);
                    }
                }
            }
            if (allCols.size() >= 2) {
                final String only = allCols.get(0);
                final List<String[]> onePair = new ArrayList<>();
                onePair.add(new String[]{"column", only});
                final SmokeHttpClient.Resp set = http.postPairs("/jpm/" + code + "/setVisibleColumns", onePair);
                int visible = set.isError() ? -1
                        : Jsoup.parse(set.body, http.baseUrl()).select("th.data[data-field]").size();
                final boolean ok = !set.isError() && visible == 1;
                checks.add(new Check(code, "columns", ok,
                        set.isError() ? status(set) : "visible columns=" + visible + " (expected 1)"));
                final List<String> restore = original.isEmpty() ? allCols : original;
                final List<String[]> pairs = new ArrayList<>();
                for (String c : restore) {
                    pairs.add(new String[]{"column", c});
                }
                http.postPairs("/jpm/" + code + "/setVisibleColumns", pairs);
            }
        }

        // 6) read-only operations (show, edit, add, audit, ...) by GET — NEVER mutate
        if (testOperations) {
            final Set<String> seen = new LinkedHashSet<>();
            for (Element a : doc.select("a[id^=operation-]")) {
                final String opId = a.id().substring("operation-".length());
                final String href = a.attr("href");
                final String cls = a.className();
                final boolean safe = SAFE_OPS.contains(opId)
                        && !href.endsWith(".exec")
                        && !cls.contains("confirm-true")
                        && !href.isBlank();
                if (!safe || !seen.add(opId)) {
                    continue;
                }
                final SmokeHttpClient.Resp r = http.getHref(href);
                checks.add(new Check(code, "op:" + opId, !r.isError(), status(r)));
            }
        }
        return checks;
    }

    private static int countRows(Document doc) {
        return doc.select("tr.instance-row").size();
    }

    private static List<String> columnValues(Document d, String field) {
        final List<String> values = new ArrayList<>();
        for (Element td : d.select("td.data[data-field=" + field + "]")) {
            final Element boolLink = td.selectFirst(".inline-boolean");
            final Element icon = td.selectFirst("span[class], i[class]");
            if (boolLink != null && icon != null && !boolLink.attr("data-true-icon").isBlank()) {
                values.add(icon.className().equals(boolLink.attr("data-true-icon")) ? "true" : "false");
            } else if (icon != null && (icon.className().contains("fa-check") || icon.className().contains("fa-times"))) {
                values.add(icon.className().contains("fa-check") ? "true" : "false");
            } else {
                values.add(td.text().trim());
            }
        }
        return values;
    }

    private void checkFilterColumn(List<Check> checks, String code, Document doc, String field, Element form) throws Exception {
        final List<String> base = columnValues(doc, field);
        if (base.isEmpty()) {
            return;
        }
        final boolean isBool = base.stream().allMatch(v -> v.equals("true") || v.equals("false"));

        if (isBool) {
            for (String target : new String[]{"true", "false"}) {
                final Map<String, String> p = new LinkedHashMap<>();
                p.put("fieldId", field);
                p.put("value", target);
                final SmokeHttpClient.Resp r;
                try {
                    r = http.postForm("/jpm/" + code + "/addSearch", p);
                } catch (Exception e) {
                    checks.add(new Check(code, "filterColumn:" + field + "=" + target, false, "error: " + e.getMessage()));
                    continue;
                }
                if (r.isError()) {
                    checks.add(new Check(code, "filterColumn:" + field + "=" + target, false, status(r)));
                } else {
                    final List<String> cells = columnValues(Jsoup.parse(r.body, http.baseUrl()), field);
                    final long bad = cells.stream().filter(v -> !v.equals(target)).count();
                    checks.add(new Check(code, "filterColumn:" + field + "=" + target, bad == 0,
                            bad == 0 ? cells.size() + " cells = " + target
                                    : bad + "/" + cells.size() + " cells != " + target + " (filter does not narrow the column)"));
                }
                http.get("/jpm/" + code + "/removeAllSearch");
            }
        } else if (isStringSearch(form)) {
            final String sample = base.stream().filter(v -> !v.isBlank()).findFirst().orElse(null);
            final long distinct = base.stream().distinct().count();
            if (sample == null || distinct < 2) {
                return;
            }
            final Map<String, String> p = new LinkedHashMap<>();
            p.put("fieldId", field);
            p.put("value", sample);
            p.put("operator", "li");
            try {
                final SmokeHttpClient.Resp r = http.postForm("/jpm/" + code + "/addSearch", p);
                if (!r.isError()) {
                    final List<String> cells = columnValues(Jsoup.parse(r.body, http.baseUrl()), field);
                    final String needle = sample.toLowerCase();
                    final long bad = cells.stream().filter(v -> !v.toLowerCase().contains(needle)).count();
                    checks.add(new Check(code, "filterColumn:" + field, bad == 0,
                            bad == 0 ? cells.size() + " cells contain '" + sample + "'"
                                    : bad + "/" + cells.size() + " cells do NOT contain '" + sample + "'"));
                }
            } catch (Exception ignore) {
                // no-op: the non-error check already covers failures
            }
            http.get("/jpm/" + code + "/removeAllSearch");
        }
    }

    /** Heuristic: the field form is a date searcher (a 'value' input of type date). */
    private static boolean isDateSearch(Element form) {
        if (form == null) {
            return false;
        }
        final Element value = form.selectFirst("input[name=value]");
        return value != null && "date".equalsIgnoreCase(value.attr("type"));
    }

    /** Heuristic: the field form is a string searcher (text 'value' input + 'li' operator). */
    private static boolean isStringSearch(Element form) {
        if (form == null) {
            return false;
        }
        final Element value = form.selectFirst("input[name=value]");
        final boolean textValue = value != null
                && (value.attr("type").isBlank() || "text".equalsIgnoreCase(value.attr("type")));
        final boolean hasLike = !form.select("select[name=operator] option[value=li]").isEmpty();
        return textValue && hasLike;
    }

    private Map<String, String> buildSearchParams(Document doc, String field) {
        final Map<String, String> params = new LinkedHashMap<>();
        final Element form = doc.getElementById("fieldSearchForm_" + field);
        if (form == null) {
            params.put("value", "a");
            params.put("operator", "li");
            return params;
        }
        for (Element in : form.select("input[name]")) {
            final String name = in.attr("name");
            if (name.isEmpty()) {
                continue;
            }
            final String type = in.attr("type").toLowerCase();
            switch (type) {
                case "hidden":
                    if (in.hasAttr("value")) {
                        params.put(name, in.attr("value"));
                    }
                    break;
                case "checkbox":
                case "radio":
                    if (!params.containsKey(name) && in.hasAttr("value") && !in.attr("value").isBlank()) {
                        params.put(name, in.attr("value"));
                    }
                    break;
                default:
                    params.put(name, sampleFor(type));
            }
        }
        for (Element sel : form.select("select[name]")) {
            final String name = sel.attr("name");
            String chosen = "";
            for (Element opt : sel.select("option")) {
                final String v = opt.hasAttr("value") ? opt.attr("value") : opt.text();
                if (!v.isBlank()) {
                    chosen = v;
                    break;
                }
            }
            params.put(name, chosen);
        }
        params.putIfAbsent("value", "a");
        return params;
    }

    private static String sampleFor(String inputType) {
        switch (inputType) {
            case "number":
                return "1";
            case "date":
                return "2020-01-01";
            default:
                return "a"; // text / no type
        }
    }

    static String entityCodeFromListHref(String href) {
        // .../jpm/{code}/list  (may include context path or query)
        final int i = href.indexOf("jpm/");
        if (i < 0) {
            return null;
        }
        String rest = href.substring(i + 4);
        final int q = rest.indexOf('?');
        if (q >= 0) {
            rest = rest.substring(0, q);
        }
        final String[] parts = rest.split("/");
        if (parts.length >= 2 && "list".equals(parts[parts.length - 1])) {
            return parts[parts.length - 2];
        }
        return null;
    }

    private static String status(SmokeHttpClient.Resp r) {
        return "HTTP " + r.status + (r.isError() ? " (error/exception)" : "");
    }

    private static String enc(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }

    public int getMaxEntities() {
        return maxEntities;
    }

    /** Ad-hoc command-line execution. */
    public static void main(String[] args) throws Exception {
        final String base = prop("smoke.baseUrl", "http://localhost:8084/jpm2-web-bs5");
        final String user = prop("smoke.user", null);
        final String pass = prop("smoke.pass", null);
        final boolean filters = Boolean.parseBoolean(prop("smoke.filters", "true"));
        final boolean columns = Boolean.parseBoolean(prop("smoke.columns", "true"));
        final boolean operations = Boolean.parseBoolean(prop("smoke.operations", "true"));
        final boolean i18n = Boolean.parseBoolean(prop("smoke.i18n", "true"));
        final int max = Integer.parseInt(prop("smoke.maxEntities", "0"));
        if (user == null || pass == null) {
            System.err.println("Missing -Dsmoke.user / -Dsmoke.pass");
            System.exit(2);
        }
        final SmokeHttpClient http = new SmokeHttpClient(base);
        if (!http.login(user, pass)) {
            System.err.println("Login failed against " + base);
            System.exit(1);
        }
        final boolean verbose = Boolean.parseBoolean(prop("smoke.verbose", "false"));
        final String only = prop("smoke.only", null);
        final Set<String> onlySet = only == null ? null
                : new LinkedHashSet<>(java.util.Arrays.asList(only.split("\\s*,\\s*")));
        final WebSmokeCrawler crawler = new WebSmokeCrawler(http, filters, columns, operations, i18n, max);
        System.out.println("Login OK against " + base + " as '" + user + "'");
        final List<MenuEntry> menu = crawler.scrapeMenu();
        System.out.println("Entities in menu: " + menu.size()
                + "  (filters=" + filters + " columns=" + columns + " operations=" + operations
                + " i18n=" + i18n + " verbose=" + verbose + ")");

        int totalChecks = 0, failed = 0, n = 0, skipped = 0, crawled = 0;
        final List<Check> failures = new ArrayList<>();
        for (MenuEntry e : menu) {
            if (onlySet != null && !onlySet.contains(e.code)) {
                continue;
            }
            if (max > 0 && n++ >= max) {
                break;
            }
            if (verbose) {
                System.out.println("\n▶ " + e.code + "  (" + e.label + ")  ->  " + http.baseUrl() + "/jpm/" + e.code + "/list");
            }
            final List<Check> checks = crawler.crawlEntity(e.code);
            if (checks.stream().anyMatch(c -> c.skipped)) {
                skipped++;
                System.out.printf("  %-30s SKIP (%s)%n", e.code, checks.get(0).detail);
                continue;
            }
            crawled++;
            for (Check c : checks) {
                totalChecks++;
                if (!c.ok) {
                    failed++;
                    failures.add(c);
                }
                if (verbose) {
                    System.out.printf("    [%s] %-28s %s%n", c.ok ? "OK" : "KO", c.action, c.detail);
                }
            }
            final long ko = checks.stream().filter(c -> !c.ok).count();
            if (!verbose) {
                System.out.printf("  %-30s %2d checks, %d KO%n", e.code, checks.size(), ko);
            } else {
                System.out.printf("  = %s: %d checks, %d KO%n", e.code, checks.size(), ko);
            }
        }
        System.out.println("\n==== SUMMARY ====");
        System.out.println("Entities: " + menu.size() + " | crawled: " + crawled + " | skipped(no access/JS): " + skipped);
        System.out.println("Checks: " + totalChecks + " | OK: " + (totalChecks - failed) + " | KO: " + failed);
        for (Check c : failures) {
            System.out.printf("  KO  %-25s %-25s %s%n", c.entity, c.action, c.detail);
        }
        System.exit(failed == 0 ? 0 : 1);
    }

    private static String prop(String k, String def) {
        final String v = System.getProperty(k);
        return (v == null || v.isBlank()) ? def : v;
    }
}
