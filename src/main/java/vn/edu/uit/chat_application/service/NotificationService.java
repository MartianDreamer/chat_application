package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.entity.FriendRequest;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ConversationService conversationService;
    public List<Notification> message(Message message) {
        return notificationRepository.saveAll(conversationService.getConversationMembers(message.getTo().getId()).stream()
                .map(ConversationMembership::getMember)
                .map(e -> new Notification(message.getId(), message.getTimestamp(), e, Notification.Type.MESSAGE))
                .toList());
    }

    public Notification friendRequest(FriendRequest friendRequest) {
        return notificationRepository.save(new Notification(friendRequest.getId(), LocalDateTime.now(), friendRequest.getTo(), Notification.Type.FRIEND_REQUEST));
    }

    public Notification friendAccept(FriendRequest friendRequest) {
        return notificationRepository.save(new Notification(friendRequest.getTo().getId(), LocalDateTime.now(), friendRequest.getFrom(), Notification.Type.FRIEND_ACCEPT));
    }
}
