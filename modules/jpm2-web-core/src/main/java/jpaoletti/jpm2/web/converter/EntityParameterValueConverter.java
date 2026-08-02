package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.crypto.SysparamCipher;
import jpaoletti.jpm2.core.entityparam.EntityParameter;
import jpaoletti.jpm2.core.entityparam.EntityParameterResolver;
import jpaoletti.jpm2.core.entityparam.EntityParameters;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Renders an {@link EntityParameter} value for list/show: plain for non-secret, masked ({@code ••••••}) for
 * secret (secrecy resolved from the {@link EntityParameterCatalog} for the converter's {@code kind}). When
 * {@code reveal} is enabled (show scope) and the user holds {@link #REVEAL_AUTHORITY}, it adds an in-place eye
 * toggle showing the decrypted value. Mirror of {@code SysparamValueConverter}.
 *
 * @author jpaoletti
 */
public class EntityParameterValueConverter extends Converter {

    /** Reuses the sysparam reveal authority so a single grant governs all secret reveals. */
    public static final String REVEAL_AUTHORITY = "jpm.sysparam.auth.revealSecret";
    private static final String MASK = "&bull;&bull;&bull;&bull;&bull;&bull;";
    private static final String EMPTY = "-";

    private String kind;
    private boolean reveal = false;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object value, String instanceId)
            throws ConverterException, ConfigurationException {
        if (!(instance instanceof EntityParameter)) {
            return plain(value);
        }
        final EntityParameter param = (EntityParameter) instance;
        final ParameterizedEntity<?> owner = param.getOwnerEntity();
        final boolean secret = (owner != null)
                ? EntityParameterResolver.isSecret(owner, param.getName())
                : (EntityParameters.catalog() != null && EntityParameters.catalog().isSecret(kind, param.getName()));
        if (!secret) {
            return plain(value);
        }
        if (param.getValue() == null) {
            return EMPTY;
        }
        final SysparamCipher cipher = EntityParameters.cipher();
        if (reveal && hasRevealAuthority() && cipher != null) {
            String decrypted;
            try {
                decrypted = cipher.decrypt(param.getValue());
            } catch (Exception e) {
                decrypted = null;
            }
            final String safe = (decrypted == null) ? "" : escape(decrypted);
            final String base = "epv-" + instanceId;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isReveal() {
        return reveal;
    }

    public void setReveal(boolean reveal) {
        this.reveal = reveal;
    }
}
