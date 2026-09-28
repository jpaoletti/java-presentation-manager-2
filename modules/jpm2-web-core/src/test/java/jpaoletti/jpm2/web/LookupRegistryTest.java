package jpaoletti.jpm2.web;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.FieldConfig;
import jpaoletti.jpm2.core.model.IdentifiableListFilter;
import jpaoletti.jpm2.web.converter.WebEditObject;
import jpaoletti.jpm2.web.search.ObjectSearcher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class LookupRegistryTest {

    private LookupRegistry registry;

    @Before
    public void setUp() {
        final Entity customer = new Entity("customer", "x.Customer");
        customer.setFields(Arrays.asList(new Field("name")));

        final WebEditObject combo = new WebEditObject();
        combo.setEntity(customer);
        combo.setTextField("{code} - {name}");
        combo.setFilter(filter("activeFilter"));
        final Field invoiceCustomer = new Field("customer");
        invoiceCustomer.setConfigs(Arrays.asList(new FieldConfig("edit add", null, combo)));

        final ObjectSearcher searcher = new ObjectSearcher();
        searcher.setEntity(customer);
        searcher.setTextField("name");
        final Field searchCustomer = new Field("searchCustomer");
        searchCustomer.setSearcher(searcher);

        final Entity invoice = new Entity("invoice", "x.Invoice");
        invoice.setFields(Arrays.asList(invoiceCustomer, searchCustomer));

        final Map<String, Entity> entities = new LinkedHashMap<>();
        entities.put("customer", customer);
        entities.put("invoice", invoice);
        final PresentationManager jpm = new PresentationManager();
        jpm.setEntities(entities);
        registry = new LookupRegistry();
        registry.setJpm(jpm);
    }

    @Test
    public void indexesConvertersAndSearchers() {
        assertEquals(2, registry.getLookups("customer").size());
        assertTrue(registry.getLookups("invoice").isEmpty());
    }

    @Test
    public void matchesTextFieldAndFilter() {
        assertEquals(1, registry.find("customer", "{code} - {name}", "activeFilter").size());
        assertEquals("customer", registry.find("customer", "{code} - {name}", "activeFilter").get(0).getSourceField().getId());
        assertTrue(registry.find("customer", "{code} - {name}", "").isEmpty());
        assertTrue(registry.find("customer", "{password}", "activeFilter").isEmpty());
    }

    @Test
    public void nullEmptyAndNullStringAreEquivalent() {
        assertEquals(1, registry.find("customer", "name", null).size());
        assertEquals(1, registry.find("customer", "name", "").size());
        assertEquals(1, registry.find("customer", "name", "null").size());
    }

    @Test
    public void declaredFilterAndTextField() {
        assertTrue(registry.isDeclaredFilter("customer", "activeFilter"));
        assertFalse(registry.isDeclaredFilter("customer", "otherFilter"));
        assertTrue(registry.isDeclaredTextField("customer", "name"));
        assertFalse(registry.isDeclaredTextField("customer", "password"));
    }

    private static IdentifiableListFilter filter(String id) {
        return () -> id;
    }
}
