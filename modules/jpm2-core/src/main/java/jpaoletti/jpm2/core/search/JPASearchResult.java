package jpaoletti.jpm2.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jpaoletti.jpm2.core.dao.IDAOListConfiguration;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
import jpaoletti.jpm2.core.message.Message;

/**
 * Implementation of ISearchResult for JPA Criteria API.
 * Uses BiFunction predicates and applies them to JPADAOListConfiguration.
 *
 * @author jpaoletti
 */
public class JPASearchResult implements ISearchResult {

    private final Message description;
    private final BiFunction<CriteriaBuilder, Root, Predicate> predicate;
    private final List<JPAAlias> aliases;

    public JPASearchResult(Message description, BiFunction<CriteriaBuilder, Root, Predicate> predicate) {
        this.description = description;
        this.predicate = predicate;
        this.aliases = new ArrayList<>();
    }

    public JPASearchResult(Message description, BiFunction<CriteriaBuilder, Root, Predicate> predicate, List<JPAAlias> aliases) {
        this.description = description;
        this.predicate = predicate;
        this.aliases = aliases;
    }

    @Override
    public Message getDescription() {
        return description;
    }

    @Override
    public void applyTo(IDAOListConfiguration configuration) {
        if (configuration instanceof JPADAOListConfiguration) {
            JPADAOListConfiguration jpaCfg = (JPADAOListConfiguration) configuration;
            jpaCfg.withPredicate(predicate);
            for (JPAAlias alias : aliases) {
                jpaCfg.withAlias(alias.getProperty(), alias.getAlias(), alias.getJoinType());
            }
        } else {
            throw new UnsupportedOperationException("JPASearchResult can only be applied to JPADAOListConfiguration. " +
                    "Use HibernateSearchResult for DAOListConfiguration.");
        }
    }

    @Override
    public boolean hasAliases() {
        return aliases != null && !aliases.isEmpty();
    }

    /**
     * Adds an alias to this search result
     *
     * @param property the property to join
     * @param alias the alias name
     * @return this instance for chaining
     */
    public JPASearchResult addAlias(String property, String alias) {
        return addAlias(property, alias, JoinType.INNER);
    }

    /**
     * Adds an alias with specific join type to this search result
     *
     * @param property the property to join
     * @param alias the alias name
     * @param joinType the join type
     * @return this instance for chaining
     */
    public JPASearchResult addAlias(String property, String alias, JoinType joinType) {
        if (!aliasExists(alias)) {
            aliases.add(new JPAAlias(property, alias, joinType));
        }
        return this;
    }

    /**
     * Checks if an alias with the given name already exists
     *
     * @param alias the alias name to check
     * @return true if the alias exists
     */
    public boolean aliasExists(String alias) {
        return aliases.stream().anyMatch(a -> a.getAlias().equalsIgnoreCase(alias));
    }

    /**
     * Gets the predicate function
     *
     * @return the predicate
     */
    public BiFunction<CriteriaBuilder, Root, Predicate> getPredicate() {
        return predicate;
    }

    /**
     * Gets the list of aliases
     *
     * @return the aliases
     */
    public List<JPAAlias> getAliases() {
        return aliases;
    }

    /**
     * Represents a JPA alias (join) configuration
     */
    public static class JPAAlias {
        private String property;
        private String alias;
        private JoinType joinType;

        public JPAAlias(String property, String alias) {
            this(property, alias, JoinType.INNER);
        }

        public JPAAlias(String property, String alias, JoinType joinType) {
            this.property = property;
            this.alias = alias;
            this.joinType = joinType;
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
