package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
        name = "T_FRIEND_RELATIONSHIP",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_1_user_2_constraint", columnNames = {"user_1_id", "user_2_id"})
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FriendRelationship implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_1_id")
    private User user_1;

    @ManyToOne
    @JoinColumn(name = "user_2_id")
    private User user_2;
}
