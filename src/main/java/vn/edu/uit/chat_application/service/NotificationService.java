package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.sent.AttachmentSentDto;
import vn.edu.uit.chat_application.dto.sent.ConversationSentDto;
import vn.edu.uit.chat_application.dto.sent.FriendRequestSentDto;
import vn.edu.uit.chat_application.dto.sent.MessageSentDto;
import vn.edu.uit.chat_application.dto.sent.NotificationSentDto;
import vn.edu.uit.chat_application.dto.sent.UserSentDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.Conversation;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.entity.FriendRequest;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.entity.UuidIdEntity;
import vn.edu.uit.chat_application.repository.AttachmentRepository;
import vn.edu.uit.chat_application.repository.CommonRepository;
import vn.edu.uit.chat_application.repository.ConversationRepository;
import vn.edu.uit.chat_application.repository.FriendRequestRepository;
import vn.edu.uit.chat_application.repository.MessageRepository;
import vn.edu.uit.chat_application.repository.NotificationRepository;
import vn.edu.uit.chat_application.repository.UserRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final String NOTIFICATION_QUEUE = "/queue/notification";
    private final NotificationRepository notificationRepository;
    private final ConversationService conversationService;
    private final RelationshipService relationshipService;
    private final FriendRequestRepository friendRequestRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    public void sendMessageNotifications(Message message) {
        MessageSentDto messageSentDto = MessageSentDto.from(message);
        List<Notification> notifications = notificationRepository.saveAll(conversationService.getConversationMembers(message.getTo().getId()).stream()
                .map(ConversationMembership::getMember)
                .map(e -> new Notification(message.getId(), message.getTimestamp(), e, Notification.Type.MESSAGE))
                .toList());
        notifications.forEach(e -> {
            NotificationSentDto notificationSentDto = NotificationSentDto.from(e, messageSentDto);
            simpMessagingTemplate.convertAndSendToUser(e.getTo().getUsername(), NOTIFICATION_QUEUE, notificationSentDto);
        });
    }

    public void sendFriendRequestNotification(FriendRequest friendRequest) {
        Notification notification = notificationRepository.save(new Notification(friendRequest.getId(), LocalDateTime.now(), friendRequest.getTo(), Notification.Type.FRIEND_REQUEST));
        FriendRequestSentDto friendRequestSentDto = FriendRequestSentDto.friendRequestWithFromUser(friendRequest);
        NotificationSentDto notificationSentDto = NotificationSentDto.from(notification, friendRequestSentDto);
        simpMessagingTemplate.convertAndSendToUser(notification.getTo().getUsername(), NOTIFICATION_QUEUE, notificationSentDto);
    }

    public void sendFriendAcceptNotification(FriendRequest friendRequest) {
        Notification notification = notificationRepository.save(new Notification(friendRequest.getTo().getId(), LocalDateTime.now(), friendRequest.getFrom(), Notification.Type.FRIEND_ACCEPT));
        UserSentDto user = UserSentDto.from(friendRequest.getTo());
        NotificationSentDto notificationSentDto = NotificationSentDto.from(notification, user);
        simpMessagingTemplate.convertAndSendToUser(notification.getTo().getUsername(), NOTIFICATION_QUEUE, notificationSentDto);
    }

    public void sendNewConversationNotification(List<ConversationMembership> memberships) {
        if (memberships.isEmpty()) {
            return;
        }
        ConversationSentDto conversation = ConversationSentDto.from(memberships.get(0).getConversation());
        List<Notification> notifications = notificationRepository.saveAll(memberships.stream()
                .map(e -> new Notification(conversation.getId(), LocalDateTime.now(), e.getMember(), Notification.Type.NEW_CONVERSATION))
                .toList());
        notifications.forEach(e -> {
            NotificationSentDto notificationSentDto = new NotificationSentDto(e.getId(), e.getTimestamp(), conversation, Notification.Type.NEW_CONVERSATION);
            simpMessagingTemplate.convertAndSendToUser(e.getTo().getUsername(), NOTIFICATION_QUEUE, notificationSentDto);
        });
    }

    public void sendAttachmentNotification(Attachment attachment) {
        AttachmentSentDto attachmentSentDto = AttachmentSentDto.from(attachment);
        List<Notification> notifications = notificationRepository.saveAll(conversationService.getConversationMembers(attachment.getTo().getId()).stream()
                .map(ConversationMembership::getMember)
                .map(e -> new Notification(attachment.getId(), attachment.getTimestamp(), e, Notification.Type.ATTACHMENT))
                .toList());
        notifications.forEach(e -> {
            NotificationSentDto notificationSentDto = NotificationSentDto.from(e, attachmentSentDto);
            simpMessagingTemplate.convertAndSendToUser(e.getTo().getUsername(), NOTIFICATION_QUEUE, notificationSentDto);
        });
    }

    public void sendOnlineStatusNotification(User user) {
        List<User> friends = relationshipService.getFriends(user.getId()).stream()
                .map(e -> e.getFirst().getId().equals(user.getId()) ? e.getSecond() : e.getFirst())
                .toList();
        friends.forEach(e -> {
            NotificationSentDto notificationSentDto = new NotificationSentDto(LocalDateTime.now(), UserSentDto.from(user), Notification.Type.ONLINE_STATUS_CHANGE);
            simpMessagingTemplate.convertAndSendToUser(e.getUsername(), "/queue/notification", notificationSentDto);
        });
    }

    public void acknowledge(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void acknowledge(List<UUID> notificationIds) {
        notificationRepository.deleteAllById(notificationIds);
    }

    public void acknowledge(UUID entityId, Notification.Type type) {
        notificationRepository.deleteByEntityIdAndType(entityId, type);
    }

    public Page<Notification> getMyNotification(int page, int size) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByToId(userId, pageable);
        Map<Notification.Type, List<UUID>> entityIdMap = createEntityIdMap(notificationPage.getContent());
        for (var entry : entityIdMap.entrySet()) {
            var contentMap = findContent(entry.getValue(), getRepository(entry.getKey()));
            notificationPage.getContent().forEach(e -> e.setObject(convert(entry.getKey(), contentMap.get(e.getEntityId()))));
        }
        return notificationPage;
    }

    private Object convert(Notification.Type type, Object object) {
        return switch (type) {
            case MESSAGE -> MessageSentDto.from((Message) object);
            case FRIEND_REQUEST -> FriendRequestSentDto.friendRequestWithFromUser((FriendRequest) object);
            case ATTACHMENT -> AttachmentSentDto.from((Attachment) object);
            case FRIEND_ACCEPT, ONLINE_STATUS_CHANGE -> UserSentDto.from((User) object);
            case NEW_CONVERSATION -> ConversationSentDto.from((Conversation) object);
        };
    }

    private CommonRepository<? extends UuidIdEntity> getRepository(Notification.Type type) {
        return switch (type) {
            case MESSAGE -> messageRepository;
            case FRIEND_REQUEST -> friendRequestRepository;
            case ATTACHMENT -> attachmentRepository;
            case FRIEND_ACCEPT, ONLINE_STATUS_CHANGE -> userRepository;
            case NEW_CONVERSATION -> conversationRepository;
        };
    }

    private Map<Notification.Type, List<UUID>> createEntityIdMap(List<Notification> notifications) {
        Map<Notification.Type, List<UUID>> result = new HashMap<>();
        notifications.forEach(e -> {
            List<UUID> ids = result.computeIfAbsent(e.getType(), arg -> new LinkedList<>());
            ids.add(e.getEntityId());
        });
        return result;
    }

    private <T extends UuidIdEntity> Map<UUID, T> findContent(List<UUID> ids, CommonRepository<T> repository) {
        return repository.findByIdIn(ids)
                .stream()
                .collect(Collectors.toMap(UuidIdEntity::getId, e -> e));
    }
}
