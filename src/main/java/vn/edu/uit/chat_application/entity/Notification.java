package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
        MESSAGE, FRIEND_REQUEST, USER_ONLINE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, length = 36)
    private UUID id;
    @Column(nullable = false)
    private UUID notificationId;
    private UUID entityId;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Type type;
}
