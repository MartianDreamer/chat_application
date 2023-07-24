package vn.edu.uit.chat_application.dto.sent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Conversation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationSentDto {
    private UUID id;
    private LocalDateTime createdAt;
    private String name;
    private List<UserSentDto> members;

    public ConversationSentDto(UUID id, LocalDateTime createdAt, String name) {
        this.id = id;
        this.createdAt = createdAt;
        this.name = name;
    }

    public static ConversationSentDto from(Conversation conversation) {
        return new ConversationSentDto(conversation.getId(), conversation.getCreatedAt(), conversation.getName());
    }
}
