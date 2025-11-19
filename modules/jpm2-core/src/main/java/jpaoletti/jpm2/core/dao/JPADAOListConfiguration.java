package jpaoletti.jpm2.core.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
    private List<BiFunction<CriteriaBuilder, Root, Predicate>> predicates = new ArrayList<>();
    private Set<JPAAlias> aliases = new LinkedHashSet<>();

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
        for (JPAAlias a : getAliases()) {
            if (a.getAlias().equals(alias)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JPADAOListConfiguration clone() {
        final JPADAOListConfiguration c = new JPADAOListConfiguration(entityManager);
        c.setFrom(getFrom());
        c.setMax(getMax());
        for (BiFunction<CriteriaBuilder, Root, Predicate> predicate : predicates) {
            c.withPredicate(predicate);
        }
        for (JPAAlias alias : aliases) {
            c.withAlias(alias.getProperty(), alias.getAlias(), alias.getJoinType());
        }
        for (DAOOrder o : getOrders()) {
            c.withOrder(o);
        }
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            c.getProperties().put(entry.getKey(), entry.getValue());
        }
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

    public List<BiFunction<CriteriaBuilder, Root, Predicate>> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<BiFunction<CriteriaBuilder, Root, Predicate>> predicates) {
        this.predicates = predicates;
    }

    public JPADAOListConfiguration withPredicate(BiFunction<CriteriaBuilder, Root, Predicate> predicate) {
        getPredicates().add(predicate);
        return this;
    }

    public Set<JPAAlias> getAliases() {
        if (aliases == null) {
            aliases = new LinkedHashSet<>();
        }
        return aliases;
    }

    public void setAliases(Set<JPAAlias> aliases) {
        this.aliases = aliases;
    }

    public JPADAOListConfiguration withAlias(String property, String alias) {
        if (!containsAlias(alias)) {
            getAliases().add(new JPAAlias(property, alias));
        }
        return this;
    }

    public JPADAOListConfiguration withAlias(String property, String alias, JoinType joinType) {
        if (!containsAlias(alias)) {
            getAliases().add(new JPAAlias(property, alias, joinType));
        }
        return this;
    }

    /**
     * Inner class for JPA alias/join configuration
     */
    public static class JPAAlias {
        private String property;
        private String alias;
        private JoinType joinType;

        public JPAAlias(String property, String alias) {
            this.property = property;
            this.alias = alias;
            this.joinType = JoinType.INNER;
        }

        public JPAAlias(String property, String alias, JoinType joinType) {
            this.property = property;
            this.alias = alias;
            this.joinType = joinType;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 71 * hash + Objects.hashCode(this.property);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JPAAlias other = (JPAAlias) obj;
            if (!Objects.equals(this.property, other.property)) {
                return false;
            }
            return true;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public JoinType getJoinType() {
            return joinType;
        }

        public void setJoinType(JoinType joinType) {
            this.joinType = joinType;
        }
    }
}
