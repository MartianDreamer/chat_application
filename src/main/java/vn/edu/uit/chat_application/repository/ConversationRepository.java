package vn.edu.uit.chat_application.repository;

import vn.edu.uit.chat_application.entity.Conversation;

public interface ConversationRepository extends CommonRepository<Conversation> {
    boolean existsByDuplicatedTwoPeopleConversationIdentifier(String duplicatedTwoPeopleConversationIdentifier);
}
