package jpaoletti.jpm2.core.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jpaoletti.jpm2.core.model.persistent.JpmMenuType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class JpmMenuTreeTest {

    private long ids = 1;

    /**
     * The user can access the "allowed" entities and has the authorities
     * MONITOR and ROLE_USER.
     */
    private final JpmMenuAccess access = new JpmMenuAccess() {
        private final Set<String> authorities = new HashSet<>(Arrays.asList("MONITOR", "ROLE_USER"));
        private final Set<String> entities = new HashSet<>(Arrays.asList("customer", "invoice/list", "invoice/report", "order!pending/list"));

        @Override
        public boolean hasAnyAuthority(List<String> list) {
            return list.stream().anyMatch(authorities::contains);
        }

        @Override
        public boolean canAccess(String entity, String operation) {
            return entities.contains(entity) || entities.contains(entity + "/" + operation);
        }

        @Override
        public String labelHtml(JpmMenuItem item) {
            return item.getLabel() == null ? "" : item.getLabel();
        }
    };

    private JpmMenuItem item(JpmMenuType type, String label, String entity, String operation, String url, String authorities, boolean active, JpmMenuItem... children) {
        return new JpmMenuItem(ids++, type, label, null, entity, operation, url, authorities, active, null, Arrays.asList(children));
    }

    private JpmMenuItem entity(String entity) {
        return item(JpmMenuType.ENTITY, entity, entity, null, null, null, true);
    }

    private JpmMenuItem group(String label, JpmMenuItem... children) {
        return item(JpmMenuType.GROUP, label, null, null, null, null, true, children);
    }

    private JpmMenuItem separator() {
        return item(JpmMenuType.SEPARATOR, null, null, null, null, null, true);
    }

    private JpmMenuItem header(String label) {
        return item(JpmMenuType.HEADER, label, null, null, null, null, true);
    }

    private static String describe(List<JpmMenuNode> nodes) {
        return nodes.stream().map(n -> {
            if (n.isSeparator()) {
                return "-";
            }
            final String text = (n.isHeader() ? "#" : "") + n.getLabelHtml();
            return n.isGroup() ? text + describe(n.getChildren()) : text;
        }).collect(Collectors.joining(",", "[", "]"));
    }

    @Test
    public void entityItemsNeedAccessToTheEntityAndOperation() {
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(group("G",
                entity("customer"),
                entity("supplier"),
                new JpmMenuItem(ids++, JpmMenuType.ENTITY, "report", null, "invoice", "report", "report.exec", null, null, true, null, null),
                item(JpmMenuType.ENTITY, "delete", "invoice", "delete", null, null, true),
                item(JpmMenuType.ENTITY, "pending", "order!pending", null, null, null, true))), access);
        assertEquals("[G[customer,report,pending]]", describe(menu));
        final List<JpmMenuNode> items = menu.get(0).getChildren();
        assertEquals("jpm/customer/list", items.get(0).getHref());
        assertEquals("menu-customer", items.get(0).getHtmlId());
        assertEquals("jpm/invoice/report.exec", items.get(1).getHref());
        assertEquals("menu-invoice-report", items.get(1).getHtmlId());
        assertEquals("menu-order.pending", items.get(2).getHtmlId());
    }

    @Test
    public void explicitAuthoritiesNeedAnyOfThem() {
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(group("G",
                item(JpmMenuType.URL, "monitor", null, null, "/monitor", "OTHER, MONITOR", true),
                item(JpmMenuType.URL, "stats", null, null, "jpm/stats", "ROLE_STATISTICS", true),
                item(JpmMenuType.ENTITY, "customer", "customer", null, null, "ROLE_STATISTICS", true))), access);
        assertEquals("[G[monitor]]", describe(menu));
        assertEquals("monitor", menu.get(0).getChildren().get(0).getHref());
        assertFalse(menu.get(0).getChildren().get(0).isAbsolute());
    }

    @Test
    public void groupsWithoutVisibleChildrenAreHidden() {
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(
                group("Empty", entity("supplier"), separator(), header("H")),
                group("Nested", group("Inner", entity("supplier"))),
                group("G", group("Inner", entity("customer"))),
                group("Restricted", entity("customer"))
        ), access);
        assertEquals("[G[Inner[customer]],Restricted[customer]]", describe(menu));
    }

    @Test
    public void groupAuthoritiesHideTheWholeGroup() {
        final JpmMenuItem restricted = item(JpmMenuType.GROUP, "R", null, null, null, "ROLE_ADMIN", true, entity("customer"));
        assertEquals("[]", describe(JpmMenuTree.visible(Arrays.asList(restricted), access)));
    }

    @Test
    public void separatorsAndHeadersAreCleaned() {
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(
                separator(),
                header("top level header"),
                group("G",
                        separator(),
                        header("H1"),
                        entity("customer"),
                        separator(),
                        separator(),
                        header("H2"),
                        entity("supplier"),
                        separator(),
                        header("H3"),
                        entity("invoice"),
                        separator())), access);
        assertEquals("[G[#H1,customer,-,#H3,invoice]]", describe(menu));
    }

    @Test
    public void inactiveAndInvalidItemsAreSkippedWithTheirChildren() {
        final JpmMenuItem invalid = new JpmMenuItem(ids++, JpmMenuType.ENTITY, "bad", null, "nope", null, null, null, true, "unknown entity nope", null);
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(
                item(JpmMenuType.GROUP, "Off", null, null, null, null, false, entity("customer")),
                group("G", invalid, item(JpmMenuType.ENTITY, "off", "customer", null, null, null, false), entity("customer"))), access);
        assertEquals("[G[customer]]", describe(menu));
    }

    @Test
    public void urls() {
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(group("G",
                item(JpmMenuType.URL, "abs", null, null, "https://example.com/x?a=1", null, true),
                item(JpmMenuType.URL, "rel", null, null, "//cdn.example.com", null, true))), access);
        assertTrue(menu.get(0).getChildren().get(0).isAbsolute());
        assertTrue(menu.get(0).getChildren().get(1).isAbsolute());
        assertTrue(JpmMenuTree.hasUnsupportedScheme("javascript:alert(1)"));
        assertTrue(JpmMenuTree.hasUnsupportedScheme("data:text/html,x"));
        assertFalse(JpmMenuTree.hasUnsupportedScheme("jpm/customer/list"));
        assertFalse(JpmMenuTree.hasUnsupportedScheme("mailto:a@b.com"));
    }

    @Test
    public void topLevelLinksAreAllowed() {
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(entity("customer"), entity("supplier")), access);
        assertEquals("[customer]", describe(menu));
    }

    @Test
    public void userPlaceholders() {
        final Map<String, Object> user = new HashMap<>();
        user.put("reseller.id", 15L);
        user.put("name", "a b&c");
        user.put("password", "secret");
        assertEquals("jpm/reseller/15/currentAccount.exec",
                JpmMenuTree.resolveUserPlaceholders("jpm/reseller/{user.reseller.id}/currentAccount.exec", user::get));
        assertEquals("x?n=a+b%26c", JpmMenuTree.resolveUserPlaceholders("x?n={user.name}", user::get));
        assertNull(JpmMenuTree.resolveUserPlaceholders("jpm/merchant/{user.merchant.id}/x", user::get));
        assertNull(JpmMenuTree.resolveUserPlaceholders("x?p={user.password}", user::get));
        assertEquals("jpm/monitor", JpmMenuTree.resolveUserPlaceholders("jpm/monitor", user::get));
    }

    @Test
    public void urlItemsWithUnresolvedPlaceholdersAreHidden() {
        final JpmMenuAccess withoutReseller = new JpmMenuAccess() {
            @Override
            public boolean hasAnyAuthority(List<String> list) {
                return true;
            }

            @Override
            public boolean canAccess(String entity, String operation) {
                return true;
            }

            @Override
            public String labelHtml(JpmMenuItem item) {
                return item.getLabel();
            }

            @Override
            public String resolveUrl(String url) {
                return JpmMenuTree.resolveUserPlaceholders(url, p -> null);
            }
        };
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(group("G",
                item(JpmMenuType.URL, "account", null, null, "jpm/reseller/{user.reseller.id}/currentAccount.exec", null, true),
                item(JpmMenuType.URL, "monitor", null, null, "jpm/monitor", null, true))), withoutReseller);
        assertEquals("[G[monitor]]", describe(menu));
    }

    @Test
    public void userConditions() {
        final Map<String, Object> user = new HashMap<>();
        user.put("patient", "P1");
        user.put("institution", null);
        user.put("tags", new java.util.ArrayList<>());
        user.put("password", "x");
        assertTrue(JpmMenuTree.matchesUserCondition("user.patient", user::get));
        assertFalse(JpmMenuTree.matchesUserCondition("!user.patient", user::get));
        assertFalse(JpmMenuTree.matchesUserCondition("user.institution", user::get));
        assertTrue(JpmMenuTree.matchesUserCondition("!user.institution", user::get));
        assertTrue(JpmMenuTree.matchesUserCondition("!user.tags", user::get));
        assertFalse(JpmMenuTree.matchesUserCondition("user.password", user::get));
        assertFalse(JpmMenuTree.matchesUserCondition("patient", user::get));
    }

    @Test
    public void itemsWithUserConditionsAreFiltered() {
        final JpmMenuAccess institutionUser = new JpmMenuAccess() {
            @Override
            public boolean hasAnyAuthority(List<String> list) {
                return true;
            }

            @Override
            public boolean canAccess(String entity, String operation) {
                return true;
            }

            @Override
            public String labelHtml(JpmMenuItem item) {
                return item.getLabel();
            }

            @Override
            public boolean matchesUserCondition(String condition) {
                return JpmMenuTree.matchesUserCondition(condition, p -> "institution".equals(p) ? 1L : null);
            }
        };
        final List<JpmMenuNode> menu = JpmMenuTree.visible(Arrays.asList(group("G",
                entity("medicalInstitution").withUserCondition("!user.institution"),
                group("Institution", entity("service")).withUserCondition("user.institution"),
                entity("turn").withUserCondition("user.patient"))), institutionUser);
        assertEquals("[G[Institution[service]]]", describe(menu));
    }
}
