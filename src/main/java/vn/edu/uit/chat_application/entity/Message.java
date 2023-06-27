package vn.edu.uit.chat_application.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
public class Message {
    private LocalDateTime timestamp;
    private String content;
    private List<Attachment> attachments;
    private long conversationId;
}
