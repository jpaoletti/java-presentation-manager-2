package jpaoletti.jpm2.core.search;

import java.util.HashMap;
import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;

/**
 *
 * @author jpaoletti
 */
public interface Searcher {

    public String visualization(Field field);

    public DescribedCriterion build(Field field, Map<String, String[]> parameters);

    public class DescribedCriterion {

        private Message description;
        private Criterion criterion;
        private Map<String, String> aliases;

        public DescribedCriterion(Message description, Criterion criterion) {
            this.description = description;
            this.criterion = criterion;
            this.aliases = new HashMap<>();
        }

        public DescribedCriterion(Message description, Criterion criterion, Map<String, String> aliases) {
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

        public Map<String, String> getAliases() {
            return aliases;
        }

        public void setAliases(Map<String, String> aliases) {
            this.aliases = aliases;
        }

        public DescribedCriterion addAlias(String a1, String a2) {
            getAliases().put(a1, a2);
            return this;
        }
    }
}
