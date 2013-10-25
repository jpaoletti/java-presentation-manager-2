package jpaoletti.jpm2.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author jpaoletti
 */
public class SecurityUtils {

    public static boolean userHasRole(String role) {
        if (role == null) {
            return true;
        }
        final Authentication authentication = getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (final GrantedAuthority auth : authentication.getAuthorities()) {
            if (role.equals(auth.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static Authentication getAuthentication() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }

    public static UserDetails getUserDetails() {
        return (UserDetails) SecurityUtils.getAuthentication().getPrincipal();
    }

}
