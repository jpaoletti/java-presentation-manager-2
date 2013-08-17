package jpaoletti.jpm2.core.model;

import java.util.Date;
import jpaoletti.jpm2.core.security.PMSecurityUser;
import jpaoletti.jpm2.util.StringEncrypter;

/**
 *
 * @author jpaoletti
 */
public class PMSession {

    private String sessionId;
    private PMSecurityUser user;
    private Date lastAccess;
    private StringEncrypter stringEncrypter;
    //private NavigationList navigationList;

    public PMSession(String id) {
        this.sessionId = id;
        this.stringEncrypter = new StringEncrypter(id);
    }

    public PMSecurityUser getUser() {
        return user;
    }

    public void setUser(PMSecurityUser user) {
        this.user = user;
    }

    public String getId() {
        return sessionId;
    }

    public void setId(String id) {
        this.sessionId = id;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    /**
     * Getter for the string encripter for this session
     *
     * @return enripter
     */
    public StringEncrypter getStringEncrypter() {
        return stringEncrypter;
    }

    public void setStringEncrypter(StringEncrypter stringEncrypter) {
        this.stringEncrypter = stringEncrypter;
    }

    /*public NavigationList getNavigationList() {
        if (navigationList == null) {
            navigationList = new NavigationList();
        }
        return navigationList;
    }*/
}
