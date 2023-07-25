package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Column;
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
import vn.edu.uit.chat_application.dto.received.AttachmentReceivedDto;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
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
    @Column(nullable = false, length = 10)
    private String fileExtension;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Transient
    private InputStream content;

    public Attachment(UUID id) {
        this.id = id;
    }

    public static Attachment from(AttachmentReceivedDto attachmentReceivedDto) {
        Conversation to = new Conversation(attachmentReceivedDto.getTo());
        return Attachment.builder()
                .to(to)
                .from(PrincipalUtils.getLoggedInUser())
                .fileExtension(attachmentReceivedDto.getExtension())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
