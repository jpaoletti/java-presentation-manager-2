package jpaoletti.jpm2.smoke;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP client with a session cookie (JSESSIONID) to crawl the JPM2 front-end.
 *
 * <p>Login is a form-encoded POST to Spring Security's {@code /login} processing URL
 * (username/password). CSRF is disabled in the testbed, so no token is needed. The
 * session cookie is kept automatically via {@link CookieManager}.
 *
 * @author jpaoletti
 */
public class SmokeHttpClient {

    private final String baseUrl;
    private final HttpClient http;

    public SmokeHttpClient(String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        final CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.http = HttpClient.newBuilder()
                .cookieHandler(cm)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public String baseUrl() {
        return baseUrl;
    }

    /** Simple response: final status + body + final URI (after redirects). */
    public static final class Resp {

        public final int status;
        public final String body;
        public final URI uri;

        Resp(int status, String body, URI uri) {
            this.status = status;
            this.body = body;
            this.uri = uri;
        }

        /** Error heuristic: 4xx/5xx, exception page, or a rendered error message. */
        public boolean isError() {
            if (status >= 400) {
                return true;
            }
            if (body == null) {
                return false;
            }
            // Grid data cells may legitimately contain error-looking text as DATA
            // (e.g. a syslog message row). Strip data rows before scanning so a
            // successful list isn't mistaken for an error page.
            final org.jsoup.nodes.Document d = org.jsoup.Jsoup.parse(body);
            d.select("tr.instance-row, td.data").remove();
            final String b = d.text();
            final String low = b.toLowerCase();
            return b.contains("org.springframework")
                    || b.contains("jpaoletti.jpm2.core.PMException")
                    || b.contains("java.lang.")
                    || b.contains("HTTP Status 500")
                    || b.contains("Exception Report")
                    || b.contains("IllegalArgumentException")
                    || b.contains("NullPointerException")
                    || b.contains("Not an entity")
                    || low.contains("unexpected exception")
                    || low.contains("whitelabel error page");
        }
    }

    public Resp get(String path) throws Exception {
        final HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        final HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(r.statusCode(), r.body(), r.uri());
    }

    /**
     * GET of an href as it appears in the HTML. JPM hrefs already include the context
     * path (e.g. {@code /jpm2-web-bs5/jpm/...}), so they resolve against the ORIGIN
     * (scheme://host:port), not against baseUrl (which already carries the context path
     * and would duplicate it).
     */
    public Resp getHref(String href) throws Exception {
        final URI target = href.startsWith("http") ? URI.create(href) : URI.create(origin() + href);
        final HttpRequest req = HttpRequest.newBuilder()
                .uri(target)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        final HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(r.statusCode(), r.body(), r.uri());
    }

    /** scheme://host[:port] of baseUrl. */
    public String origin() {
        final URI u = URI.create(baseUrl);
        final int port = u.getPort();
        return u.getScheme() + "://" + u.getHost() + (port > 0 ? ":" + port : "");
    }

    public Resp postForm(String path, Map<String, String> form) throws Exception {
        final java.util.List<String[]> pairs = new java.util.ArrayList<>();
        for (Map.Entry<String, String> e : form.entrySet()) {
            pairs.add(new String[]{e.getKey(), e.getValue()});
        }
        return postPairs(path, pairs);
    }

    /** URL-encoded POST supporting repeated keys (e.g. {@code column}). */
    public Resp postPairs(String path, java.util.List<String[]> pairs) throws Exception {
        final HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(encodePairs(pairs)))
                .build();
        final HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(r.statusCode(), r.body(), r.uri());
    }

    /**
     * Logs in against Spring Security's {@code /login}. Returns true when the session is
     * authenticated (the home no longer redirects to the login screen).
     */
    public boolean login(String user, String pass) throws Exception {
        final Map<String, String> form = new LinkedHashMap<>();
        form.put("username", user);
        form.put("password", pass);
        postForm("/login", form);
        final Resp home = get("/");
        final boolean atLogin = home.uri.getPath().endsWith("/login")
                || home.body.contains("name=\"password\"");
        return !atLogin && home.status < 400;
    }

    private static String encodePairs(java.util.List<String[]> pairs) {
        final StringBuilder sb = new StringBuilder();
        for (String[] p : pairs) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(p[0], StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(p[1] == null ? "" : p[1], StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}
