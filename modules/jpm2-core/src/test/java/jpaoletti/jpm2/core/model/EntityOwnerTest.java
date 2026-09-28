package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.dao.DAO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityOwnerTest {

    public static class Customer {

        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static class Note {

        private Long id;
        private Long reference;
        private Customer customer;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getReference() {
            return reference;
        }

        public void setReference(Long reference) {
            this.reference = reference;
        }

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
    }

    @Test
    void onlyIdStoresAndReturnsTheOwnerId() throws Exception {
        final Customer customer = new Customer();
        customer.setId(7L);
        final EntityOwner owner = owner("reference", true, customer);

        final Note note = new Note();
        note.setId(99L);
        owner.setOwnerObject(null, note, customer);
        assertEquals(7L, note.getReference());
        assertEquals(7L, owner.getOwnerObject(null, note));
    }

    @Test
    void onlyIdWithNullOwner() throws Exception {
        final EntityOwner owner = owner("reference", true, null);
        final Note note = new Note();
        owner.setOwnerObject(null, note, null);
        assertNull(note.getReference());
    }

    @Test
    void objectOwnerStoresTheInstance() throws Exception {
        final Customer customer = new Customer();
        customer.setId(7L);
        final EntityOwner owner = owner("customer", false, customer);
        final Note note = new Note();
        owner.setOwnerObject(null, note, customer);
        assertSame(customer, note.getCustomer());
        assertSame(customer, owner.getOwnerObject(null, note));
    }

    private static EntityOwner owner(String localProperty, boolean onlyId, Customer customer) {
        final DAO dao = mock(DAO.class);
        if (customer != null) {
            when(dao.getId(customer)).thenReturn(customer.getId());
        }
        final Entity ownerEntity = new Entity("customer", Customer.class.getName());
        ownerEntity.setDao(dao);
        final EntityOwner owner = new EntityOwner();
        owner.setOwner(ownerEntity);
        owner.setLocalProperty(localProperty);
        owner.setOnlyId(onlyId);
        return owner;
    }
}
