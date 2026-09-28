package jpaoletti.jpm2.core.cache;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class GeneralCacheMapTest {

    @Test
    void concurrentIncrementsAreNotLost() throws Exception {
        final GeneralCacheMap cache = new GeneralCacheMap(new HashMap<>());
        final ExecutorService pool = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 4000; i++) {
            pool.submit(() -> cache.incLong("counter", 1));
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
        assertEquals("4000", cache.get("counter"));
    }

    @Test
    void expiredKeysAreNotReturnedNorListed() throws Exception {
        final GeneralCacheMap cache = new GeneralCacheMap(new HashMap<>());
        cache.set("a", "1");
        cache.setExpiration("a", -1);
        cache.set("b", "2");
        assertNull(cache.get("a"));
        assertFalse(cache.getAll().containsKey("a"));
        assertEquals("2", cache.getAll().get("b"));
    }

    @Test
    void setIfAbsent() {
        final GeneralCacheMap cache = new GeneralCacheMap(new HashMap<>());
        assertTrue(cache.setIfAbsent("k", "v", 60000));
        assertFalse(cache.setIfAbsent("k", "w", 60000));
        assertEquals("v", cache.get("k"));
    }
}
