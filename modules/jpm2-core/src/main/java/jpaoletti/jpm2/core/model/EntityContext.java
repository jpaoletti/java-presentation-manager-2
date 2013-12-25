package jpaoletti.jpm2.core.model;

import java.util.Objects;
import jpaoletti.jpm2.core.dao.DAO;

/**
 * To much to explain...
 *
 * @author jpaoletti
 */
public class EntityContext {

    private String id;
    private EntityOwner owner;
    private DAO dao;
    private String home;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EntityOwner getOwner() {
        return owner;
    }

    public void setOwner(EntityOwner owner) {
        this.owner = owner;
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final EntityContext other = (EntityContext) obj;
        return Objects.equals(this.id, other.id);
    }

}
