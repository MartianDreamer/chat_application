package vn.edu.uit.chat_application.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn.edu.uit.chat_application.dto.received.MessageReceivedDto;
import vn.edu.uit.chat_application.dto.sent.MessageSentDto;
import vn.edu.uit.chat_application.dto.sent.NotificationSentDto;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.repository.NotificationRepository;
import vn.edu.uit.chat_application.service.ConversationService;
import vn.edu.uit.chat_application.service.MessageService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;

    @MessageMapping("/conversations/{conversationId}")
    public void sentMessage(@DestinationVariable("conversationId") UUID conversationId, @Payload String payload) {
        MessageReceivedDto messageReceivedDto = new MessageReceivedDto(conversationId, payload);
        Message message = messageService.createMessage(messageReceivedDto);
        List<Notification> notifications = notificationRepository.saveAll(conversationService.getConversationMembers(conversationId).stream()
                .map(ConversationMembership::getMember)
                .map(e -> new Notification(List.of(message.getId()), message.getTimestamp(), e, Notification.Type.MESSAGE))
                .toList());
        notifications.forEach(e -> {
            NotificationSentDto notificationSentDto = new NotificationSentDto(e.getId(), e.getTo().getId(), e.getTimestamp(), List.of(MessageSentDto.from(message)), Notification.Type.MESSAGE);
            simpMessagingTemplate.convertAndSendToUser(e.getTo().getUsername(), "/queue/notification", notificationSentDto);
        });
    }
}
