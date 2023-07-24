package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_ONLINE_USER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    @Column(nullable = false)
    private boolean online;
    @Column(nullable = false)
    private LocalDateTime lastSeen;

    public OnlineUser(User user, boolean online, LocalDateTime lastSeen) {
        this.user = user;
        this.online = online;
        this.lastSeen = lastSeen;
    }
}
