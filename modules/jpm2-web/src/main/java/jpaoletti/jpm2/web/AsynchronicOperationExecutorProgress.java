package jpaoletti.jpm2.web;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author jpaoletti
 */
public class AsynchronicOperationExecutorProgress extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("jpaoletti.jpm2.web.AsynchronicOperationExecutorProgress.handleTextMessage()");
    }
}
