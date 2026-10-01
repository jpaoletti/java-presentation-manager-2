package jpaoletti.jpm2.core.converter;

import jpaoletti.jpm2.core.model.Entity;

/** Resolves the presentation entity for a related value without changing the converter. */
@FunctionalInterface
public interface ObjectEntityResolver {

    Entity resolve(Object value, Entity defaultEntity);
}
