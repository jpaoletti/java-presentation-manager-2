package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class EntityFieldsCacheTest {

    @Test
    void concurrentFirstAccessFindsEveryField() throws Exception {
        final Entity entity = new Entity("e", "x.E");
        final List<Field> fields = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            fields.add(new Field("f" + i));
        }
        entity.setFields(fields);
        final ExecutorService pool = Executors.newFixedThreadPool(16);
        final CountDownLatch start = new CountDownLatch(1);
        final List<Future<Field>> results = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            results.add(pool.submit(() -> {
                start.await();
                return entity.getFieldById("f199", null);
            }));
        }
        start.countDown();
        for (Future<Field> r : results) {
            assertEquals("f199", r.get(10, TimeUnit.SECONDS).getId());
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
    }

    @Test
    void unknownContextUsesTheGeneralFields() throws Exception {
        final Entity entity = new Entity("e", "x.E");
        final List<Field> fields = new ArrayList<>();
        fields.add(new Field("name"));
        entity.setFields(fields);
        assertEquals("name", entity.getFieldById("name", "invented").getId());
    }
}
