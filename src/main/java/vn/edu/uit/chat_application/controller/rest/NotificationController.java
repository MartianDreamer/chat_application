package vn.edu.uit.chat_application.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.sent.NotificationSentDto;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.service.NotificationService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @DeleteMapping("/{id}")
    public void acknowledge(@PathVariable("id") UUID id) {
        notificationService.acknowledge(id);
    }


    @DeleteMapping
    public void acknowledge(@RequestBody List<UUID> ids) {
        notificationService.acknowledge(ids);
    }

    @GetMapping
    public @ResponseBody Page<NotificationSentDto> getMyNotifications(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "30") int size
    ) {
        return notificationService.getMyNotification(page, size).map(NotificationSentDto::from);
    }

    @GetMapping("/type")
    @ResponseBody
    public List<NotificationSentDto> getMyNotificationByType(
            @RequestParam(value = "type") Notification.Type type
    ) {
        return notificationService.getMyNotificationByType(type).stream().map(NotificationSentDto::from)
                .toList();
    }

    @GetMapping("/ask")
    public boolean isAcknowledged(@RequestParam("entity-id") UUID entityId, @RequestParam("type") Notification.Type type) {
        return notificationService.isAcknowledge(entityId, type);
    }
}
