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
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final NotificationService notificationService;

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectedEvent connectedEvent) {
        getUser(connectedEvent).ifPresent(user -> {
            UUID userId = user.getId();
            onlineUserService.userOnline(userId);
            user.setOnline(true);
            notificationService.sendOnlineStatusNotification(user);
        });
    }

    @EventListener
    public void handleDisconnectSocketEvent(SessionDisconnectEvent disconnectEvent) {
        getUser(disconnectEvent).ifPresent(user -> {
            UUID userId = user.getId();
            onlineUserService.userOffline(userId);
            user.setOnline(false);
            notificationService.sendOnlineStatusNotification(user);
        });
    }

    private Optional<User> getUser(AbstractSubProtocolEvent event) {
        try {
            return Optional.of((User) ((Authentication) Objects.requireNonNull(event.getUser())).getPrincipal());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
