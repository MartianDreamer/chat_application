package vn.edu.uit.chat_application.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn.edu.uit.chat_application.dto.received.MessageReceivedDto;
import vn.edu.uit.chat_application.dto.sent.NotificationSentDto;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.service.MessageService;
import vn.edu.uit.chat_application.service.NotificationService;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationService notificationService;

    @MessageMapping("/conversations/{conversationId}")
    public void sentMessage(@DestinationVariable("conversationId") UUID conversationId, @Payload String payload) {
        MessageReceivedDto messageReceivedDto = new MessageReceivedDto(conversationId, payload);
        Message message = messageService.createMessage(messageReceivedDto);
        notificationService.message(message).forEach(e -> {
            NotificationSentDto notificationSentDto = new NotificationSentDto(e.getId(), e.getTimestamp(), message, Notification.Type.MESSAGE);
            simpMessagingTemplate.convertAndSendToUser(e.getTo().getUsername(), "/queue/notification", notificationSentDto);
        });
    }
}
