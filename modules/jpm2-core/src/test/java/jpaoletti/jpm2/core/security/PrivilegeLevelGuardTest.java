package jpaoletti.jpm2.core.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class PrivilegeLevelGuardTest {

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void withoutUserEverythingIsAccessible() {
        assertTrue(PrivilegeLevelGuard.canAccess(group(0)));
        assertTrue(PrivilegeLevelGuard.canAccess(user("root", 0)));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList()));
        assertTrue(PrivilegeLevelGuard.canAccess(group(0)));
    }

    @Test
    void groupsWithEqualOrLowerPrivilege() {
        login(user("admin", 5));
        assertFalse(PrivilegeLevelGuard.canAccess(group(1)));
        assertTrue(PrivilegeLevelGuard.canAccess(group(5)));
        assertTrue(PrivilegeLevelGuard.canAccess(group(10)));
    }

    @Test
    void usersWithEqualOrLowerPrivilege() {
        login(user("admin", 5));
        assertFalse(PrivilegeLevelGuard.canAccess(user("root", 0)));
        assertTrue(PrivilegeLevelGuard.canAccess(user("other", 5)));
        assertTrue(PrivilegeLevelGuard.canAccess(user("operator", 20)));
        // A user without groups has the minimum privilege
        assertTrue(PrivilegeLevelGuard.canAccess(user("new")));
    }

    @Test
    void userCanAlwaysAccessItself() {
        final User noGroups = user("new");
        login(noGroups);
        assertTrue(PrivilegeLevelGuard.canAccess(user("new")));
        assertFalse(PrivilegeLevelGuard.canAccess(user("admin", 5)));
    }

    @Test
    void failsClosedWhenLevelCantBeComputed() {
        final User broken = new JpmUser() {
            @Override
            public Integer getMaxPrivilegeLevel() {
                throw new IllegalStateException("lazy");
            }
        };
        broken.setUsername("broken");
        login(broken);
        assertFalse(PrivilegeLevelGuard.canAccess(group(10)));
    }

    private static void login(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList()));
    }

    private static Group group(int level) {
        final Group g = new Group();
        g.setLevel(level);
        return g;
    }

    private static User user(String username, Integer... levels) {
        final User u = new JpmUser();
        u.setUsername(username);
        u.setGroups(new ArrayList<>());
        for (Integer level : Arrays.asList(levels)) {
            u.getGroups().add(group(level));
        }
        return u;
    }
}
