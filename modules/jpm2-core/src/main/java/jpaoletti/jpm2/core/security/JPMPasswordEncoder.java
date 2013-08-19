package jpaoletti.jpm2.core.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Custom password encoder using BCrypt.
 *
 * @author jpaoletti
 */
public class JPMPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence cs) {
        return BCrypt.hashpw(cs.toString(), BCrypt.gensalt());
    }

    @Override
    public boolean matches(CharSequence cs, String string) {
        return BCrypt.checkpw(cs.toString(), string);
    }
}
