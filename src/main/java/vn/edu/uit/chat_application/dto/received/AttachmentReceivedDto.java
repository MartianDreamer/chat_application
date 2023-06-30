package vn.edu.uit.chat_application.dto.received;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.dto.FromLoggedInUserDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.User;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentReceivedDto implements FromLoggedInUserDto {
    private UUID to;
    @JsonIgnore
    private User from;
    private Attachment.Type type;
    private String extension;
    private byte[] content;
}
