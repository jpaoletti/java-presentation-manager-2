package jpaoletti.jpm2.core.search;

import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;

/**
 *
 * @author jpaoletti
 */
public interface Searcher {

    public String visualization();

    public DescribedCriterion build(Field field, Map<String, String[]> parameters);

    public class DescribedCriterion {

        private Message description;
        private Criterion criterion;

        public DescribedCriterion(Message description, Criterion criterion) {
            this.description = description;
            this.criterion = criterion;
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
    }
}
