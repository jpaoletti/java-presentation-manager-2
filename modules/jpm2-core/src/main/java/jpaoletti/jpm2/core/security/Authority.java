package jpaoletti.jpm2.core.security;

import java.io.Serializable;
import java.util.Objects;
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
        if (getId() == null) {
            return "...";
        }
        return getId();
    }

    @Override
    public String getAuthority() {
        return getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Authority)) {
            return false;
        }
        final Authority other = (Authority) obj;
        return Objects.equals(this.id, other.id);
    }
}
