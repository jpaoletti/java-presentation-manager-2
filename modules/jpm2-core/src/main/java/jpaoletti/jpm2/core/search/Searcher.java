package jpaoletti.jpm2.core.search;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;
import org.hibernate.sql.JoinType;

/**
 *
 * @author jpaoletti
 */
public interface Searcher {

    public String visualization(Entity entity, Field field);

    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters);

    public class DescribedCriterion {

        private Message description;
        private Criterion criterion;
        private Set<DAOListConfiguration.DAOListConfigurationAlias> aliases;

        public DescribedCriterion(Message description, Criterion criterion) {
            this.description = description;
            this.criterion = criterion;
            this.aliases = new LinkedHashSet<>();
        }

        public DescribedCriterion(Message description, Criterion criterion, Set<DAOListConfiguration.DAOListConfigurationAlias> aliases) {
            this.description = description;
            this.criterion = criterion;
            this.aliases = aliases;
        }

        public Message getDescription() {
            return description;
        }

        public void setDescription(Message description) {
            this.description = description;
        }

        public Criterion getCriterion() {
            return criterion;
        }

        public void setCriterion(Criterion criterion) {
            this.criterion = criterion;
        }

        public Set<DAOListConfiguration.DAOListConfigurationAlias> getAliases() {
            return aliases;
        }

        public DescribedCriterion addAlias(String a1, String a2) {
            getAliases().add(new DAOListConfiguration.DAOListConfigurationAlias(a1, a2));
            return this;
        }

        public DescribedCriterion addAlias(String a1, String a2, JoinType jt) {
            getAliases().add(new DAOListConfiguration.DAOListConfigurationAlias(a1, a2, jt));
            return this;
        }
    }
}
