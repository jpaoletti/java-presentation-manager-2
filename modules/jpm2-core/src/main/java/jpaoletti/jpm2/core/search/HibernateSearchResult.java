package jpaoletti.jpm2.core.search;

import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.IDAOListConfiguration;
import jpaoletti.jpm2.core.message.Message;

/**
 * Implementation of ISearchResult for Hibernate Criteria API.
 * Wraps a DescribedCriterion and applies it to DAOListConfiguration.
 *
 * @author jpaoletti
 */
public class HibernateSearchResult implements ISearchResult {

    private final Searcher.DescribedCriterion describedCriterion;

    public HibernateSearchResult(Searcher.DescribedCriterion describedCriterion) {
        this.describedCriterion = describedCriterion;
    }

    @Override
    public Message getDescription() {
        return describedCriterion.getDescription();
    }

    @Override
    public void applyTo(IDAOListConfiguration configuration) {
        if (configuration instanceof DAOListConfiguration) {
            DAOListConfiguration dalCfg = (DAOListConfiguration) configuration;
            dalCfg.getRestrictions().add(describedCriterion.getCriterion());
            dalCfg.getAliases().addAll(describedCriterion.getAliases());
        } else {
            throw new UnsupportedOperationException("HibernateSearchResult can only be applied to DAOListConfiguration. " +
                    "Use JPASearchResult for JPADAOListConfiguration.");
        }
    }

    @Override
    public boolean hasAliases() {
        return describedCriterion.getAliases() != null && !describedCriterion.getAliases().isEmpty();
    }

    /**
     * Gets the underlying DescribedCriterion (for backward compatibility)
     *
     * @return the described criterion
     */
    public Searcher.DescribedCriterion getDescribedCriterion() {
        return describedCriterion;
    }
}
