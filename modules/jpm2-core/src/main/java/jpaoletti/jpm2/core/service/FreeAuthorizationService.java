package jpaoletti.jpm2.core.service;

/**
 * Just return true in any authorization required.
 *
 * @author jpaoletti
 */
public class FreeAuthorizationService implements AuthorizationService {

    /**
     *
     * @param roles comma separated string with roles
     * @return true if roles is null, false if no user is loged, true if the
     * loged user has any of this roles
     */
    @Override
    public boolean userHasRole(String roles) {
        return true;

    }

    /**
     *
     * @return "mock"
     */
    @Override
    public String getCurrentUsername() {
        return "no-user";
    }

}
