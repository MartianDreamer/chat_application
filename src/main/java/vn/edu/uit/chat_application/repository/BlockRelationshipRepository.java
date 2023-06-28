package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.BlockRelationship;

import java.util.UUID;

public interface BlockRelationshipRepository extends JpaRepository<BlockRelationship, UUID> {
    @Query(value = "select count(*) = 1 from BlockRelationship b " +
            "where (b.blocker.id = :blockerId and b.blocked.id = :blockedId) " +
            "or (b.blocker.id = :blockedId and b.blocked.id = :blockerId)")
    boolean existsByUserIds(UUID blockerId, UUID blockedId);

    Page<BlockRelationship> findAllByBlockerId(UUID blockerId, Pageable pageable);
}
