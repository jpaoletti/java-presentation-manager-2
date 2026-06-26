package jpaoletti.jpm2.web.controller;

import javax.transaction.Transactional;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.service.NotificationBellPayload;
import jpaoletti.jpm2.core.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NotificationController extends BaseController {

    @Autowired(required = false)
    private NotificationService notificationService;

    @GetMapping(value = "/jpm/notifications/bell", headers = "Accept=application/json")
    @ResponseBody
    @Transactional
    public NotificationBellPayload bell() {
        if (notificationService != null) {
            return notificationService.buildBellPayloadForCurrentUser();
        }
        return null;
    }

    // Sin @Transactional: la escritura (markAsRead/markAllAsRead) commitea en su
    // propia transaccion y recien despues refreshBell lee el estado ya persistido.
    // Cada llamada al service pasa por el proxy, por lo que abre su propia transaccion.
    @PostMapping(value = "/jpm/notifications/{notificationId}/read")
    @ResponseBody
    public NotificationBellPayload markAsRead(@PathVariable Long notificationId) {
        if (notificationService != null) {
            final String username = getAuthorizationService().getCurrentUsername();
            notificationService.markAsRead(notificationId, username);
            return notificationService.refreshBell(username);
        }
        return null;
    }

    @PostMapping(value = "/jpm/notifications/read-all")
    @ResponseBody
    public NotificationBellPayload markAllAsRead() {
        if (notificationService != null) {
            final String username = getAuthorizationService().getCurrentUsername();
            notificationService.markAllAsRead(username);
            return notificationService.refreshBell(username);
        }
        return null;
    }

    @PostMapping(value = "/jpm/notifications/read-all.exec")
    @ResponseBody
    public JPMPostResponse markAllAsReadExec() throws PMException {
        final String username = getAuthorizationService().getCurrentUsername();
        if (notificationService != null) {
            notificationService.markAllAsRead(username);
            notificationService.refreshBell(username);
        }
        return new JPMPostResponse(true, "/jpm/notification/list", MessageFactory.success("jpm.notification.markAll.success"));
    }
}
