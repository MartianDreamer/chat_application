package vn.edu.uit.chat_application.entity;

import java.time.LocalDateTime;

public sealed interface ConversationContent permits Message, Attachment {
    LocalDateTime getTimestamp();
}
