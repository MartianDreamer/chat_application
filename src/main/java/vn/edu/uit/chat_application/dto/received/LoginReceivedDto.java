package vn.edu.uit.chat_application.dto.received;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.dto.PasswordHolder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginReceivedDto implements PasswordHolder {
    private String username;
    private String password;
}
