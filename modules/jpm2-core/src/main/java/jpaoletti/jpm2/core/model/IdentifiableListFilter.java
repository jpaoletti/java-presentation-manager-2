package jpaoletti.jpm2.core.model;

/**
 * Common contract for list filters that can be referenced from UI components.
 */
public interface IdentifiableListFilter {

    /**
     * Returns a unique id for the filter.
     *
     * @return filter id
     */
    String getId();
}
