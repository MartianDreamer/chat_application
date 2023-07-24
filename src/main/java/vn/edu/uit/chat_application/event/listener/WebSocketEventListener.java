package vn.edu.uit.chat_application.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectedEvent sessionConnectEvent) {
        log.debug("connect {}", sessionConnectEvent);
    }

}
