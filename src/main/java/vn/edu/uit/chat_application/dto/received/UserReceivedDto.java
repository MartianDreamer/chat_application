package vn.edu.uit.chat_application.dto.received;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.dto.PasswordHolder;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserReceivedDto implements PasswordHolder {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private byte[] avatar;
    private String avatarExtension;
}
