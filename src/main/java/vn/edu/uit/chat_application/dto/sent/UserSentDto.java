package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.edu.uit.chat_application.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserSentDto {

    private UUID id;
    private String username;
    private String email;
    private String phoneNumber;
    private String avatar;
    private String avatarExtension;
    private boolean online;
    private LocalDateTime lastSeen;

    public static UserSentDto from(User user) {
        return UserSentDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .online(user.isOnline())
                .lastSeen(user.getLastSeen())
                .build();
    }
}
