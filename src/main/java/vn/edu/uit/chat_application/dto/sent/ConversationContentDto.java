package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.ConversationContent;
import vn.edu.uit.chat_application.entity.Message;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConversationContentDto {
    public enum Type {
        MESSAGE, ATTACHMENT
    }
    private Type type;
    private Object dto;
    public static ConversationContentDto from(ConversationContent content) {
        if (content instanceof Attachment attachment) {
            return new ConversationContentDto(Type.ATTACHMENT, AttachmentSentDto.from(attachment));
        }
        return new ConversationContentDto(Type.MESSAGE, MessageSentDto.from((Message) content));
    }
}
