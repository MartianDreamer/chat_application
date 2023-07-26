package vn.edu.uit.chat_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.FriendRequest;

import java.util.UUID;

public interface FriendRequestRepository extends CommonRepository<FriendRequest> {
    @Query(value = "select (count(*) > 0) from FriendRequest fr " +
            "where (fr.from.id = :fromId and fr.to.id = :toId) " +
            "or (fr.from.id = :toId and fr.to.id = :fromId)")
    boolean existsByUserIds(UUID fromId, UUID toId);

    boolean existsByIdAndToId(UUID id, UUID toId);

    @Query(value = "select count(fr.id) = 1 from FriendRequest fr where fr.id = :id and (fr.from.id = :fromOrToId or fr.to.id = :fromOrToId)")
    boolean existsByIdAndToIdOrFromId(UUID id, UUID fromOrToId);

    Page<FriendRequest> findAllByFromId(UUID fromId, Pageable pageable);
    Page<FriendRequest> findAllByToId(UUID toId, Pageable pageable);
}
