package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.received.LoginReceivedDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> authenticate(LoginReceivedDto dto) {
        User user = userRepository.findDistinctByUsername(dto.getUsername());
        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
