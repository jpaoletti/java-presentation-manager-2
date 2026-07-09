package jpaoletti.jpm2.core.model;

/**
 * Marks an entity that can be duplicated (deep-copied) by the generic
 * {@code duplicate} operation.
 *
 * @author jpaoletti
 */
public interface Duplicable {

    public Duplicable duplicate();

    public Long getId();
}
