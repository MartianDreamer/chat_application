package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "T_NOTIFICATION")
public class Notification {

    public enum Type {
        MESSAGE, FRIEND_REQUEST, ATTACHMENT, FRIEND_ACCEPT, NEW_CONVERSATION, ONLINE_STATUS_CHANGE, SEEN_BY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, length = 36)
    private UUID id;
    @Column(nullable = false, length = 36)
    UUID entityId;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "to_id")
    private User to;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Transient
    private Object object;

    public Notification(UUID entityId, LocalDateTime timestamp, User to, Type type) {
        this.entityId = entityId;
        this.timestamp = timestamp;
        this.to = to;
        this.type = type;
    }
}
