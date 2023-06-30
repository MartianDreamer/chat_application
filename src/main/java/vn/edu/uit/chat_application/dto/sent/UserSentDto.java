package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.edu.uit.chat_application.entity.User;

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
    private String confirmationString;
    private byte[] avatar;
    private String avatarExtension;

    public static UserSentDto from(User user) {
        return UserSentDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .confirmationString(user.getConfirmationString())
                .avatar(user.getAvatar())
                .build();
    }
}
