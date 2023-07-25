package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSentDto {
    UUID id;
    LocalDateTime timestamp;
    Object content;
    Notification.Type type;
}
