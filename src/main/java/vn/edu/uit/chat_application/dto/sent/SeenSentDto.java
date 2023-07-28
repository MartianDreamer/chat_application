package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SeenSentDto {
    MessageSentDto message;
    UserSentDto user;
}
