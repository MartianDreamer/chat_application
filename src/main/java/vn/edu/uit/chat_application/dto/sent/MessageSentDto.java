package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Message;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageSentDto {
    private UUID id;
    private UUID to;
    private UUID from;
    private LocalDateTime timestamp;
    private String content;

    public static MessageSentDto from(Message message) {
        return new MessageSentDto(message.getId(), message.getTo().getId(), message.getFrom().getId(), message.getTimestamp(), message.getContent());
    }
}
