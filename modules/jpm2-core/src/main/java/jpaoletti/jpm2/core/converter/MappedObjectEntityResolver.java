package jpaoletti.jpm2.core.converter;

import java.util.LinkedHashMap;
import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import org.hibernate.Hibernate;

/** Explicit class-to-entity mapping, including Hibernate proxies and mapped superclasses. */
public class MappedObjectEntityResolver implements ObjectEntityResolver {

    private Map<Class<?>, Entity> entities = new LinkedHashMap<>();

    @Override
    public Entity resolve(Object value, Entity defaultEntity) {
        if (value == null) {
            return defaultEntity;
        }
        // Initializes a proxy when needed to discover its concrete persistent subtype.
        for (Class<?> type = Hibernate.getClass(value); type != null; type = type.getSuperclass()) {
            final Entity entity = entities.get(type);
            if (entity != null) {
                return entity;
            }
        }
        return defaultEntity;
    }

    public Map<Class<?>, Entity> getEntities() {
        return entities;
    }

    public void setEntities(Map<Class<?>, Entity> entities) {
        this.entities = new LinkedHashMap<>(entities);
    }
}
