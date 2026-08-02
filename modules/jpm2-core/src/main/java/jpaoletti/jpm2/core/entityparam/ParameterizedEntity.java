package jpaoletti.jpm2.core.entityparam;

import java.util.Collections;
import java.util.List;

/**
 * Contract for a parent entity that owns a collection of {@link EntityParameter} children (the ubiquitous
 * "Entity + EntityParameter" pattern). Implementing it lets the parent delegate all its parameter resolution
 * to {@link EntityParameterResolver} (typed reads, catalog defaults, transparent secret decryption) instead
 * of copy-pasting {@code getParameter}/{@code getParameterMap}.
 *
 * @param <P> the concrete parameter child type
 * @author jpaoletti
 */
public interface ParameterizedEntity<P extends EntityParameter> {

    /** Catalog scope for this entity's parameters, e.g. {@code "gateway"}, {@code "ai-connector"}. */
    String getParameterKind();

    /** The owned parameter children (the {@code @OneToMany} collection). */
    List<P> getParameters();

    /**
     * When true, a single-name lookup prefers a {@code test-<name>} child over {@code <name>}
     * (generalizes the payment gateways' test-mode parameter override). Defaults to false.
     */
    default boolean isTestMode() {
        return false;
    }

    /**
     * This entity's own effective parameter catalog (its definitions plus, if applicable, those contributed by
     * a pluggable implementation it delegates to). Resolution consults this instance-scoped catalog first and
     * only then the global {@link EntityParameterCatalog} for the {@link #getParameterKind() kind}, so an owner
     * can carry definitions that depend on its runtime type (e.g. a gateway composing the defs of its GI)
     * without polluting a global flat namespace. Defaults to empty (global catalog only).
     *
     * @return the definitions in effect for this instance (never null)
     */
    default List<EntityParameterDef<?>> parameterCatalog() {
        return Collections.emptyList();
    }
}
