package vn.edu.uit.chat_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.uit.chat_application.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findDistinctByUsername(String username);

    @Modifying
    @Query(
            value = "update User u set u.confirmationString = null, u.active = true, u.validUntil = null " +
                    "where u.confirmationString = :confirmationString"
    )
    int activateUser(String confirmationString);

    @Modifying
    @Query(
            value = "update User u set u.confirmationString = :confirmationString " +
                    "where u.id = :id and u.active = false"
    )
    void resetConfirmationString(UUID id, String confirmationString);
}
