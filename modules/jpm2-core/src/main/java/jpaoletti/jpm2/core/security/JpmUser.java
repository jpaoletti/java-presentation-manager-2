package jpaoletti.jpm2.core.security;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Overrides User to make it specific.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "users")
public class JpmUser extends User {

}
