package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Default dao asuming an "Long id" identification property.
 *
 * @author jpaoletti
 */
public class DefaultDAO extends HibernateCriteriaDAO<Object, Long> {

    private static final org.apache.logging.log4j.Logger LOG = JPMUtils.getLogger(JPMUtils.DAO);

    private String className;

    @Override
    public Long getId(Object object) {
        try {
            final Long id = (Long) JPMUtils.get(object, "id");
            LOG.debug("getId className={} id={}", className, id);
            return id;
        } catch (ConfigurationException ex) {
            JPMUtils.getLogger().error(ex);
            return null;
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public Class<Object> getPersistentClass() {
        try {
            return (Class<Object>) Class.forName(getClassName());
        } catch (ClassNotFoundException ex) {
            JPMUtils.getLogger().error(ex);
            return null;
        }
    }
}
