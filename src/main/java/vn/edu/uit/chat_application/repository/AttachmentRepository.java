package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.chat_application.entity.Attachment;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findAllByMessageId(UUID messageId);

    List<Attachment> findAllByMessageConversationId(UUID conversationId);
}
