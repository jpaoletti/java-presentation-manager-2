package jpaoletti.jpm2.core.menu;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Builds the menu visible to a user from the cached items.
 *
 * <ul>
 * <li>Inactive and invalid items are skipped, with their children.</li>
 * <li>An item with authorities needs any of them.</li>
 * <li>A URL item with a {@code {user.<property>}} placeholder the current user
 * can't resolve is hidden.</li>
 * <li>An item with a user condition ({@code user.<property>} /
 * {@code !user.<property>}) needs the property to have a value / be
 * empty.</li>
 * <li>An ENTITY item needs access to the entity and the operation, the same
 * check the operation itself does.</li>
 * <li>A GROUP without visible children is hidden.</li>
 * <li>Headers and separators are ignored on the top level. A header followed by
 * nothing visible is removed, and separators at the start, at the end or
 * repeated are removed.</li>
 * </ul>
 *
 * @author jpaoletti
 */
public final class JpmMenuTree {

    public static final Pattern USER_CONDITION = Pattern.compile("^(!?)user\\.([A-Za-z0-9_.]+)$");
    private static final Pattern USER_PLACEHOLDER = Pattern.compile("\\{user\\.([A-Za-z0-9_.]+)\\}");

    private JpmMenuTree() {
    }

    public static List<JpmMenuNode> visible(List<JpmMenuItem> items, JpmMenuAccess access) {
        return visible(items, access, 0);
    }

    private static List<JpmMenuNode> visible(List<JpmMenuItem> items, JpmMenuAccess access, int level) {
        final List<JpmMenuNode> res = new ArrayList<>();
        for (JpmMenuItem item : items) {
            if (!item.isActive() || !item.isValid()) {
                continue;
            }
            if (!item.getAuthorities().isEmpty() && !access.hasAnyAuthority(item.getAuthorities())) {
                continue;
            }
            if (item.getUserCondition() != null && !access.matchesUserCondition(item.getUserCondition())) {
                continue;
            }
            switch (item.getType()) {
                case GROUP: {
                    final List<JpmMenuNode> children = visible(item.getChildren(), access, level + 1);
                    if (!children.isEmpty()) {
                        res.add(new JpmMenuNode(item.getType(), access.labelHtml(item), item.getIcon(), null, false, "menu-group-" + item.getId(), children));
                    }
                    break;
                }
                case ENTITY: {
                    if (access.canAccess(item.getEntity(), item.getOperation())) {
                        final String href = "jpm/" + item.getEntity() + "/" + item.getOperationPath();
                        res.add(new JpmMenuNode(item.getType(), access.labelHtml(item), item.getIcon(), href, false, htmlId(item), null));
                    }
                    break;
                }
                case URL: {
                    final String url = access.resolveUrl(item.getUrl());
                    if (url == null) {
                        break;
                    }
                    final boolean absolute = isAbsolute(url);
                    final String href = absolute ? url : url.replaceFirst("^/+", "");
                    res.add(new JpmMenuNode(item.getType(), access.labelHtml(item), item.getIcon(), href, absolute, "menu-item-" + item.getId(), null));
                    break;
                }
                default: {
                    // HEADER / SEPARATOR: only inside a group
                    if (level > 0) {
                        res.add(new JpmMenuNode(item.getType(), access.labelHtml(item), item.getIcon(), null, false, null, null));
                    }
                }
            }
        }
        return clean(res);
    }

    /**
     * Same id the static menu uses (menu-item.tag), so the active item is
     * highlighted the same way.
     */
    private static String htmlId(JpmMenuItem item) {
        final String id = "menu-" + item.getEntity().replace('!', '.');
        return JpmMenuItem.DEFAULT_OPERATION.equals(item.getOperation()) ? id : id + "-" + item.getOperation();
    }

    /**
     * Replaces the {@code {user.<property>}} placeholders of a url (for example
     * {@code jpm/reseller/{user.reseller.id}/currentAccount.exec}). Values are
     * url encoded; sensitive properties (password, token...) are not resolved.
     *
     * @param url the configured url
     * @param userProperty reads a property path of the current user
     * @return the resolved url, or null when a placeholder has no value
     */
    public static String resolveUserPlaceholders(String url, Function<String, Object> userProperty) {
        if (url == null || !url.contains("{user.")) {
            return url;
        }
        final Matcher m = USER_PLACEHOLDER.matcher(url);
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            final String property = m.group(1);
            final Object value = JPMUtils.isSensitive(property) ? null : userProperty.apply(property);
            if (value == null || String.valueOf(value).isEmpty()) {
                return null;
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param condition {@code user.<property>} (the property has a value) or
     * {@code !user.<property>} (the property is empty)
     * @param userProperty reads a property path of the current user
     * @return true when the condition is met; false for an invalid condition
     * or a sensitive property
     */
    public static boolean matchesUserCondition(String condition, Function<String, Object> userProperty) {
        final Matcher m = condition == null ? null : USER_CONDITION.matcher(condition.trim());
        if (m == null || !m.matches() || JPMUtils.isSensitive(m.group(2))) {
            return false;
        }
        final boolean present = hasValue(userProperty.apply(m.group(2)));
        return m.group(1).isEmpty() ? present : !present;
    }

    private static boolean hasValue(Object value) {
        if (value == null || Boolean.FALSE.equals(value)) {
            return false;
        }
        if (value instanceof CharSequence) {
            return ((CharSequence) value).toString().trim().length() > 0;
        }
        if (value instanceof java.util.Collection) {
            return !((java.util.Collection) value).isEmpty();
        }
        return true;
    }

    /**
     * @return true for http(s), mailto and protocol relative urls.
     */
    public static boolean isAbsolute(String url) {
        return url != null && (url.matches("(?i)^(https?|mailto):.*") || url.startsWith("//"));
    }

    /**
     * @return true when the url has a scheme other than http(s) or mailto
     * (javascript:, data:, ...), which the menu does not render.
     */
    public static boolean hasUnsupportedScheme(String url) {
        return url != null && url.matches("(?i)^[a-z][a-z0-9+.-]*:.*") && !isAbsolute(url);
    }

    private static List<JpmMenuNode> clean(List<JpmMenuNode> nodes) {
        // A header needs a link or group before the next header/separator
        final List<JpmMenuNode> withoutEmptyHeaders = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            final JpmMenuNode node = nodes.get(i);
            if (node.isHeader() && !hasContentAfter(nodes, i)) {
                continue;
            }
            withoutEmptyHeaders.add(node);
        }
        final List<JpmMenuNode> res = new ArrayList<>();
        for (JpmMenuNode node : withoutEmptyHeaders) {
            if (node.isSeparator() && (res.isEmpty() || res.get(res.size() - 1).isSeparator())) {
                continue;
            }
            res.add(node);
        }
        while (!res.isEmpty() && res.get(res.size() - 1).isSeparator()) {
            res.remove(res.size() - 1);
        }
        return res;
    }

    private static boolean hasContentAfter(List<JpmMenuNode> nodes, int index) {
        if (index + 1 >= nodes.size()) {
            return false;
        }
        final JpmMenuNode next = nodes.get(index + 1);
        return !next.isHeader() && !next.isSeparator();
    }
}
