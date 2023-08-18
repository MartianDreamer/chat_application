package vn.edu.uit.chat_application.controller.websocket;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import vn.edu.uit.chat_application.dto.received.MessageReceivedDto;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.service.MessageService;
import vn.edu.uit.chat_application.service.NotificationService;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final NotificationService notificationService;

    @MessageMapping("/conversations/{conversationId}")
    @Transactional
    public void sentMessage(@DestinationVariable("conversationId") UUID conversationId, @Payload String payload) {
        MessageReceivedDto messageReceivedDto = new MessageReceivedDto(conversationId, payload);
        Message message = messageService.createMessage(messageReceivedDto);
        notificationService.sendMessageNotifications(message);
    }
    @MessageMapping("/messages/{id}")
    public void seen(@DestinationVariable("id") UUID id) {
        User user = PrincipalUtils.getLoggedInUser();
        Message message = messageService.findById(id);
        message.getSeenBy().add(user);
        messageService.update(message);
        notificationService.sendSeenByNotification(message);
    }
}
