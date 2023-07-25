package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Attachment;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentSentDto {
    private UUID id;
    private UUID to;
    private UUID from;
    private LocalDateTime timestamp;
    private String extension;

    public static AttachmentSentDto from(Attachment attachment) {
        return new AttachmentSentDto(attachment.getId(), attachment.getTo().getId(), attachment.getFrom().getId(), attachment.getTimestamp(), attachment.getFileExtension());
    }
}
