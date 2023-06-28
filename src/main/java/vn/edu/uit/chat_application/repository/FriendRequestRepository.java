package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.FriendRequest;

import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    @Query(value = "select count(*) from FriendRequest fr " +
            "where (fr.from.id = :fromId and fr.to.id = :toId) " +
            "or (fr.from.id = :toId and fr.to.id = :fromId)")
    boolean existsByUserIds(UUID fromId, UUID toId);

    Page<FriendRequest> findAllByFromId(UUID fromId, Pageable pageable);
    Page<FriendRequest> findAllByToId(UUID toId, Pageable pageable);
}
