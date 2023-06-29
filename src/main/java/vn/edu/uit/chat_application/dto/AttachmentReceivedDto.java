package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Attachment;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentReceivedDto {
    private UUID messageId;
    private Attachment.Type type;
    private String extension;
    private byte[] content;
}
