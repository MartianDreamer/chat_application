package vn.edu.uit.chat_application.dto.received;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.dto.FromLoggedInUserDto;
import vn.edu.uit.chat_application.entity.User;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageReceivedDto implements FromLoggedInUserDto {
    @JsonIgnore
    private User from;
    private UUID to;
    private String content;
}
