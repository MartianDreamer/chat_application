package vn.edu.uit.chat_application.entity;

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
    private String id;
    private LocalDateTime timestamp;
    private String content;
    private List<Attachment> attachments;
    private boolean seen;
    private long conversationId;
}
