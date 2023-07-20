package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.aspect.annotation.FillFromUserField;
import vn.edu.uit.chat_application.dto.received.MessageReceivedDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "T_MESSAGE")
public class Message implements Serializable, ConversationContent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, length = 36)
    private UUID id;
    @ManyToOne
    private User from;
    @ManyToOne
    private Conversation to;
    private LocalDateTime timestamp;
    @JoinTable(
            name = "T_MESSAGE_SEEN_BY",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @ManyToMany
    private List<User> seenBy;
    @Column(nullable = false, columnDefinition = "TEXT")
    protected String content;

    @FillFromUserField
    public static Message from(MessageReceivedDto dto) {
        Conversation to = new Conversation(dto.getTo());
        return Message.builder()
                .to(to)
                .from(dto.getFrom())
                .timestamp(LocalDateTime.now())
                .seenBy(new LinkedList<>())
                .content(dto.getContent())
                .build();
    }

    public Message(UUID id) {
        this.id = id;
    }
}
