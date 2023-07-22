package vn.edu.uit.chat_application.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        // TODO - handle event when user disconnect
    }

    public void handleWebSocketConnectEvent(SessionConnectEvent sessionConnectEvent) {
        // TODO - handle event when user connect
    }

    public void handleWebSocketAckEven(SessionSubscribeEvent sessionSubscribeEvent) {

    }
}
