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
import vn.edu.uit.chat_application.aspect.annotation.FillFromUserField;
import vn.edu.uit.chat_application.dto.received.AttachmentReceivedDto;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_ATTACHMENT")
public class Attachment implements Serializable {
    public enum Type {
        STICKER, PICTURE, VIDEO, FILE
    }
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length =  10)
    private Type type;
    @Column(nullable = false, length = 10)
    private String fileExtension;
    @Transient
    private byte[] content;

    public Attachment(UUID id) {
        this.id = id;
    }

    @FillFromUserField
    public static Attachment from(AttachmentReceivedDto attachmentReceivedDto) {
        Conversation to = Conversation.builder().id(attachmentReceivedDto.getTo()).build();
        return Attachment.builder()
                .to(to)
                .from(attachmentReceivedDto.getFrom())
                .type(attachmentReceivedDto.getType())
                .fileExtension(attachmentReceivedDto.getExtension())
                .content(attachmentReceivedDto.getContent())
                .build();
    }
}
