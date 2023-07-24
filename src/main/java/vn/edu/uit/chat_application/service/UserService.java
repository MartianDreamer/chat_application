package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.chat_application.aspect.annotation.EncryptPassword;
import vn.edu.uit.chat_application.dto.received.UserReceivedDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.UserRepository;
import vn.edu.uit.chat_application.util.CommonUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findDistinctByUsername(username);
    }

    @EncryptPassword
    public String createUser(UserReceivedDto dto) {
        return userRepository.save(User.from(dto)).getConfirmationString();
    }

    @EncryptPassword
    public void updateUser(UUID id, UserReceivedDto dto) {
        userRepository.findById(id).ifPresentOrElse(o -> {
            CommonUtils.copyPropertiesIgnoreNull(dto, o);
            userRepository.save(o);
        }, () -> {
            throw CustomRuntimeException.notFound();
        });
    }

    public User loadByUsername(String username) {
        User user = userRepository.findDistinctByUsername(username);
        if (user == null) {
            throw CustomRuntimeException.notFound();
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
