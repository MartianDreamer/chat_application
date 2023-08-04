package vn.edu.uit.chat_application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.sent.AttachmentSentDto;
import vn.edu.uit.chat_application.dto.sent.ConversationSentDto;
import vn.edu.uit.chat_application.dto.sent.FriendRelationshipSentDto;
import vn.edu.uit.chat_application.dto.sent.FriendRequestSentDto;
import vn.edu.uit.chat_application.dto.sent.MessageSentDto;
import vn.edu.uit.chat_application.dto.sent.NotificationSentDto;
import vn.edu.uit.chat_application.dto.sent.SeenSentDto;
import vn.edu.uit.chat_application.dto.sent.UserSentDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.Conversation;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.entity.FriendRelationship;
import vn.edu.uit.chat_application.entity.FriendRequest;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.entity.UuidIdEntity;
import vn.edu.uit.chat_application.repository.AttachmentRepository;
import vn.edu.uit.chat_application.repository.CommonRepository;
import vn.edu.uit.chat_application.repository.ConversationRepository;
import vn.edu.uit.chat_application.repository.FriendRelationshipRepository;
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
    private final FriendRelationshipRepository friendRelationshipRepository;

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

    public void sendFriendAcceptNotification(FriendRelationship friendRelationship) {
        User to = friendRelationship.getTheOther(PrincipalUtils.getLoggedInUser().getId());
        Notification notification = new Notification(friendRelationship.getId(), LocalDateTime.now(), to, Notification.Type.FRIEND_ACCEPT);
        FriendRelationshipSentDto content = FriendRelationshipSentDto.from(friendRelationship, to.getId());
        NotificationSentDto notificationSentDto = NotificationSentDto.from(notification, content);
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
        List<FriendRelationship> friends = relationshipService.getFriends(user.getId());
        friends.forEach(e -> {
            User to = e.getTheOther(user.getId());
            NotificationSentDto notificationSentDto = new NotificationSentDto(LocalDateTime.now(), FriendRelationshipSentDto.from(e, to.getId()), Notification.Type.ONLINE_STATUS_CHANGE);
            simpMessagingTemplate.convertAndSendToUser(to.getUsername(), "/queue/notification", notificationSentDto);
        });
    }

    public void sendSeenByNotification(Message message) {
        conversationService.getConversationMembers(message.getTo().getId()).stream()
                .map(ConversationMembership::getMember)
                .forEach(e -> {
                    UserSentDto seenBy = UserSentDto.from(e);
                    MessageSentDto messageSentDto = MessageSentDto.from(message);
                    SeenSentDto seenSentDto = new SeenSentDto(messageSentDto, seenBy);
                    NotificationSentDto notificationSentDto = new NotificationSentDto(LocalDateTime.now(), seenSentDto, Notification.Type.SEEN_BY);
                    simpMessagingTemplate.convertAndSendToUser(e.getUsername(), "/queue/notification", notificationSentDto);
                });
    }

    public void acknowledge(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void acknowledge(List<UUID> notificationIds) {
        notificationRepository.deleteAllById(notificationIds);
    }

    @Transactional
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

    public List<Notification> getMyNotificationByType(Notification.Type type) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        List<Notification> notifications = notificationRepository.findByToIdAndType(userId, type);
        Map<UUID, ?> contentMap = findContent(notifications.stream().map(Notification::getEntityId).toList(), getRepository(type));
        notifications.forEach(e -> e.setObject(convert(type, contentMap.get(e.getEntityId()))));
        return notifications;
    }

    private Object convert(Notification.Type type, Object object) {
        return switch (type) {
            case MESSAGE -> MessageSentDto.from((Message) object);
            case FRIEND_REQUEST -> FriendRequestSentDto.friendRequestWithFromUser((FriendRequest) object);
            case ATTACHMENT -> AttachmentSentDto.from((Attachment) object);
            case ONLINE_STATUS_CHANGE -> UserSentDto.from((User) object);
            case NEW_CONVERSATION -> ConversationSentDto.from((Conversation) object);
            case FRIEND_ACCEPT -> FriendRelationshipSentDto.from((FriendRelationship) object, PrincipalUtils.getLoggedInUser().getId());
            case SEEN_BY -> null;
        };
    }

    private CommonRepository<? extends UuidIdEntity> getRepository(Notification.Type type) {
        return switch (type) {
            case MESSAGE, SEEN_BY -> messageRepository;
            case FRIEND_REQUEST -> friendRequestRepository;
            case ATTACHMENT -> attachmentRepository;
            case ONLINE_STATUS_CHANGE -> userRepository;
            case FRIEND_ACCEPT -> friendRelationshipRepository;
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
