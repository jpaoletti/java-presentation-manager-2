package jpaoletti.jpm2.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.PMCoreConstants;
import jpaoletti.jpm2.core.service.AuthorizationService;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * This is the superclass of all the core objects of Presentation Manager and it
 * provides some helpers.
 *
 * @author jpaoletti
 *
 */
public abstract class PMCoreObject implements PMCoreConstants, Serializable {

    @Autowired
    @JsonIgnore
    private PresentationManager jpm;

    @Autowired
    @JsonIgnore
    private AuthorizationService authorizationService;

    private Boolean debug;
    private boolean authorizable = true;

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

    public void checkAuthorization() throws NotAuthorizedException {
        if (getAuth() != null && !getAuthorizationService().userHasRole(getAuth())) {
            throw new NotAuthorizedException(getAuth());
        }
    }

    public String getAuth() {
        return null;
    }

    public MessageSource getMessageSource() {
        return getJpm().getMessageSource();
    }

    public String getMessage(String key, Object... params) {
        try {
            return getMessageSource().getMessage(key, params, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return key;
        }
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }

    public boolean isAuthorizable() {
        return authorizable;
    }

    public void setAuthorizable(boolean authorizable) {
        this.authorizable = authorizable;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
