package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.chat_application.entity.ConversationMembership;

import java.util.List;
import java.util.UUID;

public interface ConversationMembershipRepository extends JpaRepository<ConversationMembership, UUID> {
    boolean existsByConversationIdAndMemberId(UUID conversationId, UUID memberId);
    void deleteByConversationIdAndMemberIdIn(UUID conversationId, List<UUID> memberId);
    List<ConversationMembership> findByConversationId(UUID conversationId);
    Page<ConversationMembership> findByMemberIdOrderByConversationModifiedAtDesc(UUID memberId, Pageable pageable);
}
