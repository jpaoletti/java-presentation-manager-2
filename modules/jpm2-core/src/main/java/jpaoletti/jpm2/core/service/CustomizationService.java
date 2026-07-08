package jpaoletti.jpm2.core.service;

import java.io.InputStream;
import jpaoletti.jpm2.core.dao.JPADAO;
import jpaoletti.jpm2.core.model.persistent.Customization;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StreamUtils;

/**
 * Access to the system customization ({@link Customization}).
 *
 * To enable it in an app add to spring-jpm: &lt;bean id="customizationService" class="jpaoletti.jpm2.core.service.CustomizationService" /&gt;
 *
 * The id of the customization row is taken from the {@code customizationId}
 * property (default 1). It replaces the old configService.getLong("customization-id", 1L),
 * since JPM2 does not provide a generic ConfigService.
 *
 * @author jpaoletti
 */
public class CustomizationService extends JPMServiceBase {

    @Autowired
    @Qualifier(value = "dao-customization")
    private JPADAO customizationDAO;

    private long customizationId = 1L;

    private byte[] defaultLogo;

    /**
     * Retrieves the customization. It is intentionally NOT {@code @Transactional}:
     * it relies on the ambient session (OpenSessionInView) just like the original
     * implementation, and tolerates the absence of a session (e.g. during
     * DbMessageSource initialization) by returning defaults.
     */
    public Customization getCustomization() {
        Customization c = null;
        try {
            c = (Customization) customizationDAO.get(String.valueOf(customizationId));
        } catch (Exception e) {
        }
        if (defaultLogo == null) {
            try (InputStream is = getClass().getResourceAsStream("/defaultLogo.png")) {
                if (is != null) {
                    defaultLogo = StreamUtils.copyToByteArray(is);
                }
            } catch (Exception ex) {
                JPMUtils.getLogger().error("Error loading /defaultLogo.png", ex);
            }
        }
        if (c == null) {
            c = new Customization();
            c.setLogo(defaultLogo);
            c.setLoginLogo(defaultLogo);
        } else {
            if (c.getLoginLogo() == null) {
                c.setLoginLogo(defaultLogo);
            }
            if (c.getLogo() == null) {
                c.setLogo(defaultLogo);
            }
        }
        return c;
    }

    public long getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(long customizationId) {
        this.customizationId = customizationId;
    }

    @SuppressWarnings("unchecked")
    public JPADAO<Customization, Long> getCustomizationDAO() {
        return customizationDAO;
    }
}
