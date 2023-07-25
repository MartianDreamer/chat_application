package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnlineUserService {
    private final UserRepository userRepository;

    public void userOnline(UUID userId) {
        userRepository.updateOnlineStatusByUserId(userId, true, LocalDateTime.now());
    }

    public void userOffline(UUID userId) {
        userRepository.updateOnlineStatusByUserId(userId, false, LocalDateTime.now());
    }
}
