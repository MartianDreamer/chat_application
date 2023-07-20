package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.Message;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findAllByToId(UUID toId, Pageable pageable);

    @Query(value = "select (m.timestamp >= :validTime) from Message m where m.id = :id")
    boolean isModifiable(UUID id, LocalDateTime validTime);

    @Modifying
    @Query(value = "update Message m set m.content = :content where m.id = :id")
    void updateContent(UUID id, String content);
}
