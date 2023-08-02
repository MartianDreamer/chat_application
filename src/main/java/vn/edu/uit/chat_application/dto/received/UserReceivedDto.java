package vn.edu.uit.chat_application.dto.received;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.dto.PasswordHolder;
import vn.edu.uit.chat_application.validation.PhoneNumber;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserReceivedDto implements PasswordHolder {
    @Size(min = 5, max = 20, message = "username is too short")
    private String username;
    @Size(min = 8, max = 30, message = "invalid password")
    private String password;
    @Email(message = "invalid email")
    private String email;
    @PhoneNumber
    private String phoneNumber;
}
