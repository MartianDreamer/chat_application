package vn.edu.uit.chat_application.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.chat_application.aspect.annotation.EncryptPassword;
import vn.edu.uit.chat_application.aspect.authorization.annotations.SingleWriteMethod;
import vn.edu.uit.chat_application.dto.received.UserReceivedDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findDistinctByUsername(username);
    }

    @EncryptPassword
    public String createUser(UserReceivedDto dto) {
        return userRepository.save(User.from(dto)).getConfirmationString();
    }

    @EncryptPassword
    @SingleWriteMethod
    @Transactional
    public void updateUser(UUID id, UserReceivedDto dto) {
        if (dto.getPassword() != null) {
            entityManager.createNamedQuery(User.UPDATE_WITH_PASSWORD)
                    .setParameter("id", id)
                    .setParameter("username", dto.getUsername())
                    .setParameter("password", dto.getPassword())
                    .setParameter("email", dto.getEmail())
                    .setParameter("phoneNumber", dto.getPhoneNumber())
                    .setParameter("avatar", dto.getAvatar())
                    .setParameter("avatarExtension", dto.getAvatarExtension())
                    .executeUpdate();
        }
        entityManager.createNamedQuery(User.UPDATE_WITHOUT_PASSWORD)
                .setParameter("id", id)
                .setParameter("username", dto.getUsername())
                .setParameter("email", dto.getEmail())
                .setParameter("phoneNumber", dto.getPhoneNumber())
                .setParameter("avatar", dto.getAvatar())
                .setParameter("avatarExtension", dto.getAvatarExtension())
                .executeUpdate();
    }

    public User loadByUsername(String username) {
        User user = userRepository.findDistinctByUsername(username);
        if (user == null) {
            throw new CustomRuntimeException("user with username " + username + " not found", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @Transactional
    public boolean activateUser(String confirmationString) {
        if (userRepository.activateUser(confirmationString, LocalDate.now()) == 0) {
            throw new CustomRuntimeException("invalid confirmation", HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(CustomRuntimeException::notFound);
    }

    public static String generateConfirmationString() {
        return RandomStringUtils.random(40, true, true) + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
