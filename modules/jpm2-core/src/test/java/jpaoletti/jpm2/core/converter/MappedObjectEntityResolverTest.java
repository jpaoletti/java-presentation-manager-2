package jpaoletti.jpm2.core.converter;

import java.util.LinkedHashMap;
import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MappedObjectEntityResolverTest {

    static class ProductBase {
    }

    static class Product extends ProductBase {
    }

    static class Supply extends ProductBase {
    }

    @Test
    void choosesConcreteEntityAndClosestMappedSuperclass() {
        final Entity base = new Entity();
        final Entity product = new Entity();
        final Entity supply = new Entity();
        final Map<Class<?>, Entity> mapping = new LinkedHashMap<>();
        mapping.put(ProductBase.class, base);
        mapping.put(Product.class, product);
        mapping.put(Supply.class, supply);
        final MappedObjectEntityResolver resolver = new MappedObjectEntityResolver();
        resolver.setEntities(mapping);

        assertSame(product, resolver.resolve(new Product(), base));
        assertSame(supply, resolver.resolve(new Supply(), base));
        assertSame(product, resolver.resolve(new Product() {}, base));
        assertSame(base, resolver.resolve(new ProductBase() {}, null));
    }

    @Test
    void preservesDefaultForNullAndUnmappedValues() {
        final Entity fallback = new Entity();
        final MappedObjectEntityResolver resolver = new MappedObjectEntityResolver();
        assertSame(fallback, resolver.resolve(null, fallback));
        assertSame(fallback, resolver.resolve(new Object(), fallback));
    }

    @Test
    void resolvesProxyUsingItsConcreteImplementation() {
        final Entity supply = new Entity();
        final MappedObjectEntityResolver resolver = new MappedObjectEntityResolver();
        resolver.setEntities(Map.of(Supply.class, supply));
        final HibernateProxy proxy = mock(HibernateProxy.class);
        final LazyInitializer initializer = mock(LazyInitializer.class);
        when(proxy.getHibernateLazyInitializer()).thenReturn(initializer);
        when(initializer.getPersistentClass()).thenReturn(ProductBase.class);
        when(initializer.getImplementation()).thenReturn(new Supply());

        assertSame(supply, resolver.resolve(proxy, new Entity()));
    }

    @Test
    void copiesConfiguredMappingAndDoesNotMutateFallback() {
        final Entity product = new Entity();
        final Entity fallback = new Entity();
        final Map<Class<?>, Entity> mapping = new LinkedHashMap<>();
        mapping.put(Product.class, product);
        final MappedObjectEntityResolver resolver = new MappedObjectEntityResolver();
        resolver.setEntities(mapping);
        mapping.clear();

        assertSame(product, resolver.resolve(new Product(), fallback));
        assertSame(fallback, resolver.resolve(new Supply(), fallback));
        assertSame(product, resolver.resolve(new Product(), fallback));
    }
}
