package jpaoletti.jpm2.core.service;

import java.io.Serializable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author jpaoletti
 */
public class SpringAuthorizationService implements AuthorizationService, Serializable {

    /**
     *
     * @param roles comma separated string with roles
     * @return true if roles is null, false if no user is loged, true if the
     * loged user has any of this roles
     */
    @Override
    public boolean userHasRole(String roles) {
        if (roles == null) {
            return true;
        }
        final Authentication authentication = getAuthentication();
        if (authentication == null) {
            return false;
        }
        String[] split = roles.split("[,]");
        for (String role : split) {
            for (final GrantedAuthority auth : authentication.getAuthorities()) {
                if (role.equals(auth.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return the logged username or null if there is none
     */
    @Override
    public String getCurrentUsername() {
        try {
            return getUserDetails().getUsername();
        } catch (Exception e) {
            return null;
        }
    }

    protected Authentication getAuthentication() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }

    protected UserDetails getUserDetails() {
        return (UserDetails) getAuthentication().getPrincipal();
    }

}
