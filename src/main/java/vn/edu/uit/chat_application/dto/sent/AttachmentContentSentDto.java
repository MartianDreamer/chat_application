package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentContentSentDto {
    private UUID id;
    private byte[] content;
}
