package jpaoletti.jpm2.core.model;

import java.util.Arrays;
import jpaoletti.jpm2.core.service.AuthorizationService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FieldShouldDisplayTest {

    @Test
    void withoutConfigsTheDisplayPropertyDecides() {
        final Field field = new Field("error");
        field.setDisplay("show");
        assertTrue(field.shouldDisplay("show"));
        assertFalse(field.shouldDisplay("list"));
        assertFalse(field.shouldDisplay("toExcel"));
    }

    @Test
    void displayStillDecidesAfterTheImplicitConfigIsCreated() {
        // EntityInstanceList, LookupRegistry, etc. call getConfigs()/getConverter() first
        final Field field = new Field("error");
        field.setDisplay("show");
        assertTrue(field.getConfigs().size() == 1);
        assertTrue(field.hasImplicitConfigs());
        assertFalse(field.shouldDisplay("list"));
        assertTrue(field.shouldDisplay("show"));
        assertFalse(field.hasConfigFor("list"));
    }

    @Test
    void explicitConfigsAreNotImplicit() {
        final Field field = new Field("name");
        field.setConfigs(Arrays.asList(config(null, null, "ROLE_OTHER")));
        assertFalse(field.hasImplicitConfigs());
    }

    @Test
    void withoutConfigsAndDisplayAllIsShownEverywhere() {
        final Field field = new Field("name");
        assertTrue(field.shouldDisplay("list"));
        assertTrue(field.shouldDisplay("edit"));
        assertFalse(field.shouldDisplay(null));
    }

    @Test
    void unauthorizedConfigHidesTheField() {
        final Field field = new Field("salary");
        field.setConfigs(Arrays.asList(config("list show", "ROLE_HR", "ROLE_OTHER")));
        assertFalse(field.shouldDisplay("list"));
    }

    @Test
    void firstAuthorizedConfigWins() {
        final Field field = new Field("salary");
        field.setConfigs(Arrays.asList(
                config("list", "ROLE_HR", "ROLE_OTHER"),
                config("list", null, "ROLE_OTHER")));
        assertTrue(field.shouldDisplay("list"));
    }

    @Test
    void negatedAuthIsHonored() {
        final Field field = new Field("note");
        field.setConfigs(Arrays.asList(config("list", "!ROLE_TEST", "ROLE_OTHER")));
        assertTrue(field.shouldDisplay("list"));
        final Field hidden = new Field("note");
        hidden.setConfigs(Arrays.asList(config("list", "!ROLE_TEST", "ROLE_TEST")));
        assertFalse(hidden.shouldDisplay("list"));
    }

    @Test
    void operationWithoutConfigFallsBackToDisplay() {
        final Field field = new Field("code");
        field.setDisplay("list show");
        field.setConfigs(Arrays.asList(config("edit", null, "ROLE_OTHER")));
        assertTrue(field.shouldDisplay("list"));
        assertFalse(field.shouldDisplay("add"));
    }

    private static FieldConfig config(String operations, String auth, String... roles) {
        final FieldConfig config = new FieldConfig(operations, auth, null);
        config.setAuthorizationService(new AuthorizationService() {
            @Override
            public String getCurrentUsername() {
                return "test";
            }

            @Override
            public boolean userHasRole(String role) {
                return role == null || Arrays.asList(roles).contains(role);
            }
        });
        return config;
    }
}
