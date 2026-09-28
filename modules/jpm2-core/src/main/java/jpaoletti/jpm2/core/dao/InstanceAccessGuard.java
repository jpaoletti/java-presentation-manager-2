package jpaoletti.jpm2.core.dao;

/**
 * Optional interface for DAOs whose instances are not accessible to every user
 * that can access the entity (for example users and groups filtered by
 * privilege level). JPMService checks it when an operation loads, updates,
 * deletes or saves an instance. It is not applied to plain dao.get() calls, so
 * login, converters and system code keep working.
 *
 * @author jpaoletti
 */
public interface InstanceAccessGuard {

    /**
     * @param instance a non null instance of the DAO class
     * @return true if the current user can access (manage) the instance
     */
    boolean canAccess(Object instance);
}
