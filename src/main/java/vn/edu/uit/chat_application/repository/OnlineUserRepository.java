package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.OnlineUser;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OnlineUserRepository extends JpaRepository<OnlineUser, UUID> {
    @Modifying
    @Query(
            value = "update OnlineUser u set " +
                    "u.online = :online, " +
                    "u.lastSeen = :lastSeen " +
                    "where u.user.id = :userId"
    )
    void updateByUserId(UUID userId, boolean online, LocalDateTime lastSeen);

    boolean existsByUserId(UUID userId);
}
