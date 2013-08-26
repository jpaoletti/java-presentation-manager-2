package jpaoletti.jpm2.core.security;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "authorities")
public class Authority implements Serializable {

    @Id
    @Column(name = "authority")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getId();
    }
}
