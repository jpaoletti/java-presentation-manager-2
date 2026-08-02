package jpaoletti.jpm2.core.entityparam;

import java.util.Collections;
import java.util.List;

/**
 * An application-provided contribution to the entity-parameter catalog. Each app declares one or more
 * {@code EntityParameterModule} beans exposing the managed parameter definitions for its parameterized
 * entities; the {@link EntityParameterCatalog} collects them all at startup. Definitions carry their own
 * {@code kind}, so a single module may contribute to several kinds.
 *
 * @author jpaoletti
 */
public interface EntityParameterModule {

    /** @return the managed parameter definitions this module contributes (each carrying its {@code kind}). */
    default List<EntityParameterDef<?>> params() {
        return Collections.emptyList();
    }
}
