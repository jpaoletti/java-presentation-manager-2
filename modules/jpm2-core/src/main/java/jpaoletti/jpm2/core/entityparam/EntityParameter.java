package jpaoletti.jpm2.core.entityparam;

import jpaoletti.jpm2.core.sysparam.SysparamType;

/**
 * Contract for a single name/value parameter that belongs to a {@link ParameterizedEntity}. It is a plain
 * interface (not a base class) so it composes with any persistence root — jpm2-core children extend
 * {@code JPMPersistentObject}, hermes children extend {@code CustomModelObject}, etc. A consumer can either
 * implement this directly on its existing child entity or extend {@link AbstractEntityParameter} to also
 * inherit the {@code name}/{@code value}/{@code param_type} columns.
 *
 * <p>Typing, secrecy and defaults are driven by the code-declared {@link EntityParameterCatalog} (scoped by
 * the owner's {@code kind}), not by this row — {@link #getType()} is only a stored hint/override used for
 * display and health. Resolution and decryption live in {@link EntityParameterResolver}.
 *
 * @author jpaoletti
 */
public interface EntityParameter {

    /** Persistent id of the parameter row (used to build its setValue URL in the tree view). */
    Long getId();

    String getName();

    String getValue();

    void setValue(String value);

    /** Stored type hint (nullable). Authoritative typing/secrecy comes from the catalog. */
    SysparamType getType();

    /**
     * The owning {@link ParameterizedEntity}, when reachable from the child (typically the {@code @ManyToOne}
     * back-reference). Child-side code (the value converter and the setValue executor) uses it to resolve the
     * owner's instance-scoped catalog. Defaults to null (fall back to the global catalog by kind).
     */
    default ParameterizedEntity<?> getOwnerEntity() {
        return null;
    }
}
