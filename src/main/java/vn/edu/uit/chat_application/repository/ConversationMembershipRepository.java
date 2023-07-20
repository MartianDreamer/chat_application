package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.chat_application.entity.ConversationMembership;

import java.util.UUID;

public interface ConversationMembershipRepository extends JpaRepository<ConversationMembership, UUID> {
    boolean existsByConversationIdAndMemberId(UUID conversationId, UUID memberId);
    void deleteByConversationIdAndMemberId(UUID conversationId, UUID memberId);
}
