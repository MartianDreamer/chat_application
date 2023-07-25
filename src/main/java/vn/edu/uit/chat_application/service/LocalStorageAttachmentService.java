package vn.edu.uit.chat_application.service;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.chat_application.dto.sent.AttachmentContentDto;
import vn.edu.uit.chat_application.entity.Attachment;

import java.util.List;
import java.util.UUID;

public class LocalStorageAttachmentService implements AttachmentService {
    @Override
    public Attachment create(UUID toId, List<MultipartFile> multipartFiles) {
        return null;
    }

    @Override
    public void delete(UUID attachmentId) {

    }

    @Override
    public List<AttachmentContentDto> getContents(UUID attachmentId) {
        return null;
    }
}
