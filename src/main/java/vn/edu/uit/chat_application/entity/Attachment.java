package vn.edu.uit.chat_application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.dto.AttachmentDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_ATTACHMENT")
public class Attachment {
    public enum Type {
        STICKER, PICTURE, VIDEO, FILE;
    }
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    private Type type;
    private String name;
}
