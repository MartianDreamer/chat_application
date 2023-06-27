package vn.edu.uit.chat_application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("messages")
@CompoundIndex(
        name = "conversationId_timestamp",
        unique = true,
        def = "{ 'conversationId': 1, 'timestamp': -1}"
)
public class Message {
    @Id
    protected Long creatorUserId;
    @JsonIgnore
    protected String id;
    protected LocalDateTime timestamp;
    protected String content;
    protected List<Attachment> attachments;
    protected List<Long> seenBy;
    protected Long conversationId;
}
