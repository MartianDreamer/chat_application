package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.List;
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
        MESSAGE, FRIEND_REQUEST, ATTACHMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, length = 36)
    private UUID id;
    @ElementCollection
    List<UUID> entityId;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "to_id")
    private User to;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Type type;

    public Notification(List<UUID> entityId, LocalDateTime timestamp, User to, Type type) {
        this.entityId = entityId;
        this.timestamp = timestamp;
        this.to = to;
        this.type = type;
    }
}
