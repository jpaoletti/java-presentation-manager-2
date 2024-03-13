package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Component
public class AuthenticationEventListener {

    @Autowired
    private PresentationManager jpm;

    @EventListener
    @Transactional
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        try {
            if (event instanceof AuthenticationSuccessEvent) {
                authenticationSuccess((AuthenticationSuccessEvent) event);
            }
            if (event instanceof AbstractAuthenticationFailureEvent) {
                if (event instanceof AuthenticationFailureBadCredentialsEvent) {
                    authenticationFailed((AuthenticationFailureBadCredentialsEvent) event);
                }
            }
        } catch (EntityNotFoundException e) {
        }
    }

    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) throws EntityNotFoundException {
        if (getJpm().getMaxLoginAttemps() > 0) {
            final String username = (String) event.getAuthentication().getPrincipal();
            //Increment fail count
            final User user = (User) getUserDao().get(username);
            if (user != null) {
                user.setLoginAttemps(user.getLoginAttemps() + 1);
                if (user.getLoginAttemps() >= getJpm().getMaxLoginAttemps()) {
                    user.setAccountNonLocked(false);
                }
                getUserDao().update(user);
            }
        }
    }

    public void authenticationSuccess(AuthenticationSuccessEvent event) throws EntityNotFoundException {
        if (getJpm().getMaxLoginAttemps() > 0) {
            final String username = ((User) event.getAuthentication().getPrincipal()).getUsername();
            //Reset attemps
            final User user = (User) getUserDao().get(username);
            if (user != null) {
                user.setLoginAttemps(0);
                getUserDao().update(user);
            }
        }
    }

    protected DAO getUserDao() throws EntityNotFoundException {
        return getJpm().getEntity("jpm-entity-user").getDao();
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }

}
