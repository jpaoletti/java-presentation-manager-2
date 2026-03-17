package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.service.AuthorizationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldConfigAuthTest {

    @Test
    void checkAuthorizationShouldAllowPositiveRole() {
        AuthorizationService authz = authz("ROLE_TEST");

        FieldConfig config = new FieldConfig("all", "ROLE_TEST", null);
        config.setAuthorizationService(authz);

        assertDoesNotThrow(config::checkAuthorization);
    }

    @Test
    void checkAuthorizationShouldDenyPositiveRoleWhenMissing() {
        AuthorizationService authz = authz("ROLE_OTHER");

        FieldConfig config = new FieldConfig("all", "ROLE_TEST", null);
        config.setAuthorizationService(authz);

        assertThrows(NotAuthorizedException.class, config::checkAuthorization);
    }

    @Test
    void matchShouldApplyWhenNegatedRoleIsMissing() throws Exception {
        AuthorizationService authz = authz("ROLE_OTHER");

        FieldConfig config = new FieldConfig("all", "!ROLE_TEST", null);
        config.setAuthorizationService(authz);

        Operation operation = new Operation("list");
        assertTrue(config.match(null, operation));
    }

    @Test
    void matchShouldNotApplyWhenNegatedRoleIsPresent() throws Exception {
        AuthorizationService authz = authz("ROLE_TEST");

        FieldConfig config = new FieldConfig("all", "!ROLE_TEST", null);
        config.setAuthorizationService(authz);

        Operation operation = new Operation("list");
        assertFalse(config.match(null, operation));
    }

    private AuthorizationService authz(String... roles) {
        return new AuthorizationService() {
            @Override
            public String getCurrentUsername() {
                return "test-user";
            }

            @Override
            public boolean userHasRole(String role) {
                if (role == null) {
                    return true;
                }
                for (String r : roles) {
                    if (role.equals(r)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
