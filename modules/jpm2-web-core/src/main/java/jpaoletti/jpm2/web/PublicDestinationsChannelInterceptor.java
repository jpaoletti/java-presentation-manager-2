package jpaoletti.jpm2.web;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.AntPathMatcher;

/**
 * Inbound channel interceptor for applications whose websocket endpoint must
 * accept anonymous connections (for example a public waiting room screen).
 * Connections without an authenticated user can only subscribe to the
 * configured public destinations and can't send messages. Authenticated
 * connections are not restricted.
 *
 * Usage:
 * <pre>
 * &lt;websocket:message-broker ...&gt;
 *     ...
 *     &lt;websocket:client-inbound-channel&gt;
 *         &lt;websocket:interceptors&gt;
 *             &lt;beans:bean class="jpaoletti.jpm2.web.PublicDestinationsChannelInterceptor"&gt;
 *                 &lt;beans:property name="publicDestinations" value="/app/public/**" /&gt;
 *             &lt;/beans:bean&gt;
 *         &lt;/websocket:interceptors&gt;
 *     &lt;/websocket:client-inbound-channel&gt;
 * &lt;/websocket:message-broker&gt;
 * </pre>
 *
 * @author jpaoletti
 */
public class PublicDestinationsChannelInterceptor implements ChannelInterceptor {

    private final AntPathMatcher matcher = new AntPathMatcher();
    private List<String> publicDestinations = new ArrayList<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        final StompCommand command = accessor.getCommand();
        if (accessor.getUser() != null || command == null) {
            return message;
        }
        if (command == StompCommand.SEND
                || (command == StompCommand.SUBSCRIBE && !isPublic(accessor.getDestination()))) {
            JPMUtils.getLogger().debug("PublicDestinationsChannelInterceptor DENIED command={} destination={}", command, accessor.getDestination());
            throw new MessageDeliveryException("Access denied to " + accessor.getDestination());
        }
        return message;
    }

    protected boolean isPublic(String destination) {
        if (destination == null) {
            return false;
        }
        for (String pattern : getPublicDestinations()) {
            if (matcher.match(pattern.trim(), destination)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getPublicDestinations() {
        return publicDestinations;
    }

    /**
     * Ant style patterns of the destinations anonymous connections can
     * subscribe to. A comma separated string is also accepted from XML.
     */
    public void setPublicDestinations(List<String> publicDestinations) {
        this.publicDestinations = publicDestinations;
    }
}
