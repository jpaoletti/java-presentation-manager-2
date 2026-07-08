package jpaoletti.jpm2.web.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.service.CustomizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Serves the system customization assets (login logo, favicon and dynamic
 * stylesheet) from
 * {@link jpaoletti.jpm2.core.model.persistent.Customization}.
 *
 * Opt-in: requires the {@code customizationService} bean
 * (&lt;bean id="customizationService" class="jpaoletti.jpm2.core.service.CustomizationService" /&gt;).
 * If it is not declared, the endpoints degrade gracefully returning empty.
 *
 * The top logo (logosite.png) is intentionally NOT served here: it usually has
 * application-specific logic (e.g. per-participant logo), so each app resolves
 * it in its own controller.
 *
 * @author jpaoletti
 */
@Controller
public class CustomizationController extends BaseController {

    @Autowired(required = false)
    private CustomizationService customizationService;

    @ResponseBody
    @GetMapping(value = "/static/img/login.png", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] loginLogo(HttpServletResponse response) throws IOException {
        if (customizationService == null) {
            return null;
        }
        return customizationService.getCustomization().getLoginLogo();
    }

    @ResponseBody
    @GetMapping(value = "/static/favicon.ico", produces = "image/x-icon")
    public byte[] favicon(HttpServletResponse response) throws IOException {
        if (customizationService == null) {
            return null;
        }
        return customizationService.getCustomization().getFavicon();
    }

    @GetMapping(value = "/static/css/customization_style.css")
    public ResponseEntity<Void> customizationCss(HttpServletResponse response) throws IOException {
        response.setContentType("text/css");
        if (customizationService != null) {
            final String style = customizationService.getCustomization().getStyle();
            if (style != null) {
                response.getOutputStream().print(style);
            }
        }
        response.flushBuffer();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
