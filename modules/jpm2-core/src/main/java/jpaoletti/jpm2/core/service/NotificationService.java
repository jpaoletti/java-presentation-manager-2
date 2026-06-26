package jpaoletti.jpm2.core.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import jpaoletti.jpm2.core.dao.DAOOrder;
import jpaoletti.jpm2.core.dao.JPADAO;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
import jpaoletti.jpm2.core.model.persistent.Notification;
import jpaoletti.jpm2.core.model.persistent.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * incluye in spring-jpm : <bean id="notificationService" class="jpaoletti.jpm2.core.service.NotificationService" />
 * @author jpaoletti
 */
public class NotificationService extends JPMServiceBase {

    private static final int BELL_LIMIT = 10;

    @Autowired
    @Qualifier(value = "dao-notification")
    private JPADAO notificationDAO;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired(required = false)
    private SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public Notification create(String username, NotificationType type, String description) {
        Notification notification = new Notification();
        notification.setUsername(username);
        notification.setType(type);
        notification.setDescription(description);
        notification.setCreatedAt(new Date());
        notification.setRead(false);
        notificationDAO.save(notification);
        // create() se ejecuta dentro de la transaccion del proceso que la dispara
        // (compras, stock, etc). El push del websocket se difiere hasta despues del
        // commit, para que el conteo/listado reflejen la notificacion ya persistida.
        publishBellAfterCommit(username);
        return notification;
    }

    @Transactional
    public void markAsRead(Long notificationId, String username) {
        Notification notification = findByIdAndUsername(notificationId, username);
        if (notification != null && !notification.isRead()) {
            notification.setRead(true);
            notificationDAO.update(notification);
        }
    }

    @Transactional
    public void markAllAsRead(String username) {
        JPADAOListConfiguration cfg = notificationDAO.build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("username"), username));
        cfg.withPredicate((cb, root) -> cb.equal(root.get("read"), false));
        for (Notification notification : getNotificationDAO().list(cfg)) {
            notification.setRead(true);
            notificationDAO.update(notification);
        }
    }

    /**
     * Construye el payload de la campana y lo emite por websocket. Debe
     * invocarse en una transaccion distinta y posterior a la de escritura
     * (markAsRead / markAllAsRead), de modo que las consultas vean los cambios
     * ya commiteados.
     */
    @Transactional
    public NotificationBellPayload refreshBell(String username) {
        publishBell(username);
        return buildBellPayload(username);
    }

    @Transactional
    public long countUnreadByUser(String username) {
        JPADAOListConfiguration cfg = notificationDAO.build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("username"), username));
        cfg.withPredicate((cb, root) -> cb.equal(root.get("read"), false));
        return notificationDAO.count(cfg);
    }

    @Transactional
    public List<Notification> listLatestUnreadByUser(String username, int limit) {
        JPADAOListConfiguration cfg = getNotificationDAO().build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("username"), username));
        cfg.withPredicate((cb, root) -> cb.equal(root.get("read"), false));
        cfg.withOrder(new DAOOrder("createdAt", false));
        cfg.withOrder(new DAOOrder("id", false));
        cfg.setMax(limit);
        return notificationDAO.list(cfg);
    }

    @Transactional
    public NotificationBellPayload buildBellPayload(String username) {
        NotificationBellPayload payload = new NotificationBellPayload();
        payload.setUnreadCount(countUnreadByUser(username));
        payload.setViewAllUrl("/jpm/notification/list");
        payload.setItems(
                listLatestUnreadByUser(username, BELL_LIMIT).stream()
                        .map(this::toPayloadItem)
                        .collect(Collectors.toList())
        );
        return payload;
    }

    @Transactional
    public NotificationBellPayload buildBellPayloadForCurrentUser() {
        return buildBellPayload(authorizationService.getCurrentUsername());
    }

    @Transactional
    public Notification findByIdAndUsername(Long notificationId, String username) {
        JPADAOListConfiguration cfg = notificationDAO.build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("id"), notificationId));
        cfg.withPredicate((cb, root) -> cb.equal(root.get("username"), username));
        return (Notification) notificationDAO.find(cfg);
    }

    protected void publishBell(String username) {
        if (simpMessagingTemplate != null) {
            simpMessagingTemplate.convertAndSendToUser(username, "/notifications", buildBellPayload(username));
        }
    }

    /**
     * Difiere el push de la campana hasta despues del commit de la transaccion
     * en curso (la sesion OSIV sigue abierta, por lo que buildBellPayload puede
     * consultar los datos ya persistidos). Si no hay transaccion activa, emite
     * en el momento.
     *
     * @param username
     */
    protected void publishBellAfterCommit(final String username) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishBell(username);
                }
            });
        } else {
            publishBell(username);
        }
    }

    private NotificationBellPayloadItem toPayloadItem(Notification notification) {
        NotificationBellPayloadItem item = new NotificationBellPayloadItem();
        item.setId(notification.getId());
        item.setDescription(notification.getDescription());
        item.setCreatedAt(notification.getCreatedAt().toString());
        item.setTypeCssClass(notification.getType().getCssClass());
        item.setTypeIconClass(notification.getType().getIconClass());
        return item;
    }

    @SuppressWarnings("unchecked")
    public JPADAO<Notification, Long> getNotificationDAO() {
        return notificationDAO;
    }
}
