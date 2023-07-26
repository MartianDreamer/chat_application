package vn.edu.uit.chat_application.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.chat_application.dto.sent.AttachmentContentDto;
import vn.edu.uit.chat_application.entity.Attachment;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    Attachment create(UUID toId, List<MultipartFile> multipartFiles);
    void delete(UUID attachmentId);
    List<AttachmentContentDto> getContents(UUID attachmentId);
}
