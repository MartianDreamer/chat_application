package vn.edu.uit.chat_application.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.service.NotificationService;
import vn.edu.uit.chat_application.service.OnlineUserService;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final NotificationService notificationService;

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectedEvent connectedEvent) {
        User user = getUser(connectedEvent);
        UUID userId = user.getId();
        onlineUserService.userOnline(userId);
        user.setOnline(true);
        notificationService.sendOnlineStatusNotification(user);
    }

    @EventListener
    public void handleDisconnectSocketEvent(SessionDisconnectEvent disconnectEvent) {
        User user = getUser(disconnectEvent);
        UUID userId = user.getId();
        onlineUserService.userOffline(userId);
        user.setOnline(false);
        notificationService.sendOnlineStatusNotification(user);
    }

    private User getUser(AbstractSubProtocolEvent event) {
        return (User) ((Authentication) Objects.requireNonNull(event.getUser())).getPrincipal();
    }
}
