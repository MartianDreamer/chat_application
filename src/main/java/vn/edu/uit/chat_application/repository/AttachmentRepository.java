package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.Attachment;

import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    @Query(
            value = "select count(a) from Attachment a " +
                    "where a.id = :id and " +
                    "a.to.id in " +
                    "(select distinct(c.conversation.id) from ConversationMembership c where c.member.id = :userId)"
    )
    boolean existsByIdAndMemberId(UUID id, UUID userId);

    boolean existsByIdAndFromId(UUID id, UUID fromId);
}
