package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.persistent.Sysparam;
import jpaoletti.jpm2.core.service.SysparamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Renders a {@link Sysparam} value for list/show: plain for non-secret, masked
 * ({@code ••••••}) for secret. When {@code reveal} is enabled (show scope) and the current
 * user holds {@value #REVEAL_AUTHORITY}, it adds an in-place "eye" toggle that reveals the
 * decrypted value. This keeps everything inside the standard {@code show} operation — no
 * separate "effective value" operation needed.
 *
 * @author jpaoletti
 */
public class SysparamValueConverter extends Converter {

    public static final String REVEAL_AUTHORITY = "jpm.sysparam.auth.revealSecret";
    private static final String MASK = "&bull;&bull;&bull;&bull;&bull;&bull;";
    /** Placeholder for an unset value: keeps the field non-empty so it still renders in show. */
    private static final String EMPTY = "-";

    private boolean reveal = false;

    @Autowired(required = false)
    private SysparamService service;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object value, String instanceId)
            throws ConverterException, ConfigurationException {
        if (!(instance instanceof Sysparam)) {
            return plain(value);
        }
        final Sysparam param = (Sysparam) instance;
        if (!param.isSecret()) {
            return plain(value);
        }
        // Secret from here on: never render the stored (encrypted) value.
        if (param.getValue() == null) {
            return EMPTY;
        }
        if (reveal && hasRevealAuthority() && service != null) {
            String decrypted;
            try {
                // Decrypt the stored value directly (we are already in the secret branch),
                // not via getRaw, whose decryption is gated on the catalog knowing the key.
                decrypted = service.revealSecretValue(param.getValue());
            } catch (Exception e) {
                decrypted = null;
            }
            final String safe = (decrypted == null) ? "" : escape(decrypted);
            final String base = "spv-" + instanceId;
            return "<span id=\"" + base + "\">" + MASK + "</span>"
                    + "<span id=\"" + base + "-c\" style=\"display:none\">" + safe + "</span>"
                    + "<a href=\"#\" class=\"ms-2\" onclick=\""
                    + "var m=document.getElementById('" + base + "'),c=document.getElementById('" + base + "-c');"
                    + "if(c.style.display==='none'){c.style.display='';m.style.display='none';this.firstChild.className='fas fa-eye-slash';}"
                    + "else{c.style.display='none';m.style.display='';this.firstChild.className='fas fa-eye';}return false;\">"
                    + "<i class=\"fas fa-eye\"></i></a>";
        }
        return MASK;
    }

    private boolean hasRevealAuthority() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (REVEAL_AUTHORITY.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private static String plain(Object value) {
        final String v = (value == null) ? "" : value.toString();
        return v.isEmpty() ? EMPTY : escape(v);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    public boolean isReveal() {
        return reveal;
    }

    public void setReveal(boolean reveal) {
        this.reveal = reveal;
    }
}
