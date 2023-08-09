package vn.edu.uit.chat_application.exception;

import lombok.Getter;
import vn.edu.uit.chat_application.entity.Conversation;
import vn.edu.uit.chat_application.entity.User;

@Getter
public class DuplicateConversationException extends Exception {
    private final Conversation conversation;
    private final User creator;

    public DuplicateConversationException(String message, Conversation conversation, User user) {
        super(message);
        this.conversation = conversation;
        this.creator = user;
    }
}
