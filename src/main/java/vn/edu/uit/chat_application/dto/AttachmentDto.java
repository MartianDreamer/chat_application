package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Attachment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentDto {
    private Attachment.Type type;
    private String name;
    private byte[] content;
}
