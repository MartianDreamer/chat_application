package vn.edu.uit.chat_application.dto.sent;

import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.ConversationContent;
import vn.edu.uit.chat_application.entity.Message;

public sealed interface ConversationContentDto permits AttachmentSentDto, MessageSentDto {
    static ConversationContentDto from(ConversationContent content) {
        if (content instanceof Attachment attachment) {
            return AttachmentSentDto.from(attachment);
        }
        return MessageSentDto.from((Message) content);
    }
}
