package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Generic JPA DAO for entities whose id is an {@code Integer} business key. The persistent class is
 * set via {@code className} and the id property via {@code idName} (default {@code "codigo"}), so
 * it can be wired entirely from the entity XML without a dedicated DAO class.
 *
 * @author jpaoletti
 */
public class DefaultIntegerIdJPADAO extends JPADAO<Object, Integer> {

    private String className;
    private String idName = "codigo";

    @Override
    public Integer getId(Object object) {
        try {
            return (Integer) JPMUtils.get(object, getIdName());
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

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }
}
