package vn.edu.uit.chat_application.dto.received;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentReceivedDto {
    private UUID to;
    private String extension;
    private MultipartFile content;
}
