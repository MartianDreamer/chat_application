package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.Conversation;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    boolean existsByDuplicatedTwoPeopleConversationIdentifier(String duplicatedTwoPeopleConversationIdentifier);

    @Query(value = "from Conversation c where c.id in (select m from ConversationMembership m where m.member.id = :userId)")
    Page<Conversation> findByMemberId(UUID userId, Pageable pageable);
}
