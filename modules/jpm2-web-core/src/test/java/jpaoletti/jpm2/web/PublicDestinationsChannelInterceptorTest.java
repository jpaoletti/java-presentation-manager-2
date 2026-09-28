package jpaoletti.jpm2.web;

import java.util.Arrays;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class PublicDestinationsChannelInterceptorTest {

    private PublicDestinationsChannelInterceptor interceptor;

    @Before
    public void setUp() {
        interceptor = new PublicDestinationsChannelInterceptor();
        interceptor.setPublicDestinations(Arrays.asList("/siach3WS/waitingRoom*"));
    }

    @Test
    public void anonymousCanSubscribeToPublicDestination() {
        assertNotNull(interceptor.preSend(frame(StompCommand.SUBSCRIBE, "/siach3WS/waitingRoom12", false), null));
    }

    @Test(expected = MessageDeliveryException.class)
    public void anonymousCantSubscribeToOtherDestinations() {
        interceptor.preSend(frame(StompCommand.SUBSCRIBE, "/asynchronicOperationExecutor/progress/user#1", false), null);
    }

    @Test(expected = MessageDeliveryException.class)
    public void anonymousCantSend() {
        interceptor.preSend(frame(StompCommand.SEND, "/siach3WS/waitingRoom12", false), null);
    }

    @Test
    public void anonymousCanConnect() {
        assertNotNull(interceptor.preSend(frame(StompCommand.CONNECT, null, false), null));
    }

    @Test
    public void authenticatedIsNotRestricted() {
        assertNotNull(interceptor.preSend(frame(StompCommand.SUBSCRIBE, "/asynchronicOperationExecutor/progress/user#1", true), null));
        assertNotNull(interceptor.preSend(frame(StompCommand.SEND, "/jpm/x", true), null));
    }

    private static Message<byte[]> frame(StompCommand command, String destination, boolean authenticated) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        if (destination != null) {
            accessor.setDestination(destination);
        }
        if (authenticated) {
            accessor.setUser(new UsernamePasswordAuthenticationToken("user", null));
        }
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
