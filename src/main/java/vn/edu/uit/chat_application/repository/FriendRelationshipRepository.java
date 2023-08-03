package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.FriendRelationship;

import java.util.List;
import java.util.UUID;

public interface FriendRelationshipRepository extends CommonRepository<FriendRelationship> {
    @Query(value = "select count(fs) = 1 from FriendRelationship fs " +
            "where (fs.first.id = :firstId and fs.second.id = :secondId) " +
            "or (fs.first.id = :secondId and fs.second.id = :firstId)")
    boolean existsByUserIds(UUID firstId, UUID secondId);

    Page<FriendRelationship> findAllByFirstIdOrSecondId(UUID firstId, UUID secondId, Pageable pageable);

    List<FriendRelationship> findAllByFirstIdOrSecondId(UUID firstId, UUID secondId);
}
