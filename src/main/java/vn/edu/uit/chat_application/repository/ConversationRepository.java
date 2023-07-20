package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.chat_application.entity.Conversation;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    boolean existsByDuplicatedTwoPeopleConversationIdentifier(String duplicatedTwoPeopleConversationIdentifier);
}
