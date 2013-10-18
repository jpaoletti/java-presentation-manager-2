package jpaoletti.jpm2.core.security;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "authorities")
public class Authority implements GrantedAuthority, Serializable {

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

    @Override
    public String getAuthority() {
        return getId();
    }
}
