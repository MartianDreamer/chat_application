package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.FriendRelationship;

import java.util.UUID;

public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship, UUID> {
    @Query(value = "select count(*) = 1 from FriendRelationship fs " +
            "where (fs.first.id = :first and fs.second.id = :second) " +
            "or (fs.first.id = :second and fs.second.id = :first)")
    boolean existsByUserIds(UUID firstId, UUID secondId);

    Page<FriendRelationship> findAllByFirstIdOrSecondId(UUID userId, Pageable pageable);
}
