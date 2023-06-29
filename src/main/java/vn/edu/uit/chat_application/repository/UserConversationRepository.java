package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.chat_application.entity.UserConversation;

import java.util.UUID;

public interface UserConversationRepository extends JpaRepository<UserConversation, UUID> {
    boolean existsByConversationIdAndMemberId(UUID conversationId, UUID memberId);
}
