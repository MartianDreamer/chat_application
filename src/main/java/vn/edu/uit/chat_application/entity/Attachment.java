package vn.edu.uit.chat_application.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_ATTACHMENT")
public final class Attachment implements Serializable, ConversationContent, UuidIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "to_id")
    private Conversation to;
    @ManyToOne
    @JoinColumn(name = "from_id")
    private User from;
    @ElementCollection
    @CollectionTable(name = "T_ATTACHMENT_FILE")
    private List<String> fileName;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Attachment(UUID id) {
        this.id = id;
    }

    public Attachment(Conversation to, User from, List<String> fileName, LocalDateTime timestamp) {
        this.to = to;
        this.from = from;
        this.fileName = fileName;
        this.timestamp = timestamp;
    }
}
