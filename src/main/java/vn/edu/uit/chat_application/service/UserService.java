package vn.edu.uit.chat_application.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.chat_application.dto.UserReceivedDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadByUsername(username);
    }

    public User saveUser(UserReceivedDto dto) {
        return userRepository.save(User.from(dto));
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
        long epoch = Long.parseLong(confirmationString.substring(40));
        LocalDateTime invalidTimestamp = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.now();
        if (now.minusDays(1).isAfter(invalidTimestamp)) {
            throw new CustomRuntimeException("invalid confirmation", HttpStatus.BAD_REQUEST);
        }
        return userRepository.activateUser(confirmationString) != 0;
    }

    public String resetConfirmationString(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new CustomRuntimeException("user with id" + id + " not found", HttpStatus.NOT_FOUND);
        }
        String confirmationString = generateConfirmationString();
        userRepository.resetConfirmationString(id, generateConfirmationString());
        return confirmationString;
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(CustomRuntimeException::notFound);
    }

    public static String generateConfirmationString() {
        return RandomStringUtils.random(40, true, true) + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
