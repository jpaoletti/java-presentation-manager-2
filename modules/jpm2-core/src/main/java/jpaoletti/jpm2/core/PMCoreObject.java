package jpaoletti.jpm2.core;

import jpaoletti.jpm2.core.model.PMCoreConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This is the superclass of all the core objects of Presentation Manager and it
 * provides some helpers.
 *
 * @author jpaoletti
 *
 */
public abstract class PMCoreObject implements PMCoreConstants {

    private Boolean debug;

    /**
     * Display a debug information on PM log if debug flag is active
     *
     * @param s String information
     */
    public void debug(String s) {
        if (getDebug()) {
            System.out.println(s);
        }
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the debug
     */
    public Boolean getDebug() {
        if (debug == null) {
            return false;
        }
        return debug;
    }

    public boolean userHasRole(String role) {
        // get security context from thread local
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return false;
        }

        final Authentication authentication = context.getAuthentication();
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
}
