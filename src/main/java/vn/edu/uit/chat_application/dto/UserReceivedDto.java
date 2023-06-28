package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserReceivedDto {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private byte[] avatar;
    private String avatarExtension;
}
