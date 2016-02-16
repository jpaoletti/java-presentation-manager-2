package jpaoletti.jpm2.core.service;

/**
 *
 * @author jpaoletti
 */
public interface AuthorizationService {

    /**
     *
     * @return the logged username or null if there is none
     */
    public String getCurrentUsername();

    /**
     *
     * @param roles comma separated string with roles
     * @return true if roles is null, false if no user is loged, true if the
     * loged user has any of this roles
     */
    public boolean userHasRole(String roles);

}
