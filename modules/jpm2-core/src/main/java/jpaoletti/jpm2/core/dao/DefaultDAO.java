package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Default dao asuming an "Long id" identification property.
 *
 * @author jpaoletti
 */
public class DefaultDAO extends HibernateCriteriaDAO<Object, Long> {

    private String className;

    @Override
    public Long getId(Object object) {
        try {
            return (Long) JPMUtils.get(object, "id");
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
