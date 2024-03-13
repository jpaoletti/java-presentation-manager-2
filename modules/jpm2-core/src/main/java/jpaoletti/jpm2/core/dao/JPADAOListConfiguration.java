package jpaoletti.jpm2.core.dao;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

/**
 *
 * @author jpaoletti
 */
public class JPADAOListConfiguration implements IDAOListConfiguration {

    private Integer from;
    private Integer max;
    private Map<String, String> properties = new LinkedHashMap<>();
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private List<DAOOrder> orders = new LinkedList<>();

    public JPADAOListConfiguration(EntityManager em) {
        this.entityManager = em;
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    public final JPADAOListConfiguration withOrder(DAOOrder order) {
        getOrders().add(order);
        return this;
    }

    public JPADAOListConfiguration clearOrders() {
        getOrders().clear();
        return this;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public boolean containsAlias(String alias) {
        return false; //TODO
    }

    @Override
    public JPADAOListConfiguration clone() {
        final JPADAOListConfiguration c = new JPADAOListConfiguration(entityManager);
        c.setFrom(getFrom());
        c.setMax(getMax());
        return c;
    }

    @Override
    public Integer getFrom() {
        return from;
    }

    @Override
    public Integer getMax() {
        return max;
    }

    @Override
    public void setFrom(Integer from) {
        this.from = from;
    }

    @Override
    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    public JPADAOListConfiguration withMax(Integer max) {
        setMax(max);
        return this;
    }

    @Override
    public JPADAOListConfiguration withFrom(Integer from) {
        setFrom(from);
        return this;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public List<DAOOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<DAOOrder> orders) {
        this.orders = orders;
    }
}
