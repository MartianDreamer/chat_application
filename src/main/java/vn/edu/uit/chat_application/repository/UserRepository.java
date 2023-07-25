package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findDistinctByUsername(String username);

    @Modifying
    @Query(
            value = "update User u set u.confirmationString = null, u.active = true, u.validUntil = null " +
                    "where u.confirmationString = :confirmationString and u.validUntil >= :now"
    )
    int activateUser(String confirmationString, LocalDate now);

    @Modifying
    @Query(
            value = "update User u set " +
                    "u.online = :online, " +
                    "u.lastSeen = :lastSeen " +
                    "where u.id = :userId"
    )
    void updateOnlineStatusByUserId(UUID userId, boolean online, LocalDateTime lastSeen);

    @Modifying
    @Query(
            value = "update User u " +
                    "set u.avatar = :avatar, " +
                    "u.avatarExtension = :extension " +
                    "where u.id = :userId"
    )
    void uploadAvatar(UUID userId, String extension, byte[] avatar);
}
