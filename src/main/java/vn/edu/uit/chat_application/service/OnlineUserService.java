package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.entity.OnlineUser;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.repository.OnlineUserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnlineUserService {
    private final OnlineUserRepository onlineUserRepository;

    public void userOnline(UUID userId) {
        if (onlineUserRepository.existsByUserId(userId)) {
            onlineUserRepository.updateByUserId(userId, true, LocalDateTime.now());
            return;
        }
        OnlineUser onlineUser = new OnlineUser(new User(userId), true, LocalDateTime.now());
        onlineUserRepository.save(onlineUser);
    }

    public void userOffline(UUID userId) {
        onlineUserRepository.updateByUserId(userId, false, LocalDateTime.now());
    }
}
