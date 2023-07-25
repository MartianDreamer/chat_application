package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;
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
public final class Attachment implements Serializable, ConversationContent {
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
    private List<String> fileName;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Transient
    private InputStream content;

    public Attachment(UUID id) {
        this.id = id;
    }
}
