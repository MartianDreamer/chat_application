package vn.edu.uit.chat_application.dto.received;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.chat_application.dto.FromLoggedInUserDto;
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
    private String extension;
    private MultipartFile content;
}
