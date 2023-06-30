package vn.edu.uit.chat_application.service;

import vn.edu.uit.chat_application.dto.received.AttachmentReceivedDto;
import vn.edu.uit.chat_application.entity.Attachment;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    List<Attachment> createAttachments(List<AttachmentReceivedDto> dtos);
    void deleteAttachmens(List<UUID> ids);
    List<Attachment> getAttachments(List<UUID> ids);
    List<Attachment> getByConversationId(UUID conversationId);
    void persistAttachment(Attachment attachment);
    void deleteAttachmentFile(Attachment attachment);
    Attachment getAttachmentContent(Attachment attachment);
}
