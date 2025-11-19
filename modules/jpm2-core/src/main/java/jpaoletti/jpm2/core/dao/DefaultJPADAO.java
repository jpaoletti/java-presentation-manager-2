package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Default JPA DAO assuming a "Long id" identification property.
 *
 * This is a configurable DAO that can be used with any entity class
 * that has a Long id field. The entity class is specified via the
 * className property, which can be set through Spring configuration.
 *
 * Example Spring configuration:
 * <pre>
 * &lt;bean id="myEntityDAO" class="jpaoletti.jpm2.core.dao.DefaultJPADAO"&gt;
 *     &lt;property name="className" value="com.mycompany.model.MyEntity"/&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * @author jpaoletti
 */
public class DefaultJPADAO extends JPADAO<Object, Long> {

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

    /**
     * @return the fully qualified class name of the entity
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the fully qualified class name of the entity that this DAO will manage.
     *
     * @param className the fully qualified class name (e.g., "com.mycompany.model.User")
     */
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public Class<Object> getPersistentClass() {
        if (className == null) {
            JPMUtils.getLogger().error("className property is not set for DefaultJPADAO");
            return null;
        }
        try {
            return (Class<Object>) Class.forName(getClassName());
        } catch (ClassNotFoundException ex) {
            JPMUtils.getLogger().error("Class not found: " + getClassName(), ex);
            return null;
        }
    }
}
