package jpaoletti.jpm2.core;

import jpaoletti.jpm2.core.model.PMCoreConstants;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This is the superclass of all the core objects of Presentation Manager and it
 * provides some helpers.
 *
 * @author jpaoletti
 *
 */
public abstract class PMCoreObject implements PMCoreConstants {

    @Autowired
    private MessageSource messageSource;
    private Boolean debug;

    /**
     * Display a debug information on PM log if debug flag is active
     *
     * @param s String information
     */
    public void debug(String s) {
        if (getDebug()) {
            JPMUtils.getLogger().debug(debug);
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

    public Authentication getAuthentication() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }

    public void checkAuthorization() throws NotAuthorizedException {
        if (getAuth() != null && !userHasRole(getAuth())) {
            throw new NotAuthorizedException();
        }
    }

    /**
     * Override me
     */
    public String getAuth() {
        return null;
    }

    protected UserDetails getUserDetails() {
        return (UserDetails) getAuthentication().getPrincipal();
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... params) {
        try {
            return getMessageSource().getMessage(key, params, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return key;
        }
    }
}
