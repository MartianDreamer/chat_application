package vn.edu.uit.chat_application.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

@Entity
@Table(name = "T_CONVERSATION")
@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Conversation implements Serializable, UuidIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(length = 30)
    private String name;
    @Column(length = 100)
    private String duplicatedTwoPeopleConversationIdentifier;
    @ElementCollection
    @CollectionTable(
            name = "T_CONVERSATION_MUTER",
            uniqueConstraints = {
                    @UniqueConstraint(name = "conversation_muter_constraint", columnNames = {"muter_id", "conversation_id"})
            }
    )
    @Column(name = "muter_id")
    private List<UUID> muterIds;

    public Conversation(UUID id) {
        this.id = id;
    }
}
