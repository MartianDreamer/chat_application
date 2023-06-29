package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.Conversation;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentSentDto {
    private UUID id;
    private Conversation conversation;
    private Attachment.Type type;
    private UserSentDto from;
    private LocalDateTime timestamp;
}