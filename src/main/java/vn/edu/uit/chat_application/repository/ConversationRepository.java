package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.Conversation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends CommonRepository<Conversation> {
    boolean existsByDuplicatedTwoPeopleConversationIdentifier(String duplicatedTwoPeopleConversationIdentifier);

    @Modifying
    @Query(
            value = "insert into t_conversation_muter(conversation_id, muter_id) values (:conversationId, :muterId)",
            nativeQuery = true
    )
    void insertIntoTConversationMuter(UUID conversationId, UUID muterId);

    @Modifying
    @Query(
            value = "delete from t_conversation_muter where " +
                    "conversation_id = :conversationId and " +
                    "muter_id = :muterId",
            nativeQuery = true
    )
    void deleteConversationMuterByConversationId(UUID conversationId, UUID muterId);


    @Query(
            value = "select * from t_conversation c where" +
                    " c.id in " +
                    "(select m.conversation_id from t_conversation_muter m where m.muter_id = :muterId)",
            nativeQuery = true
    )
    List<Conversation> findConversationByMuterId(UUID muterId);

    @Modifying
    @Query(
            value = "update Conversation c set c.modifiedAt = :modifiedAt where " +
                    "c.id = :id"
    )
    void updateByIdSetModifiedAt(UUID id, LocalDateTime modifiedAt);
}
