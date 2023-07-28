package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.received.MessageReceivedDto;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    @Value("${app.message-modifiability-duration}")
    private long messageModifiabilityDuration;

    public Message createMessage(MessageReceivedDto dto) {
        return messageRepository.save(Message.from(dto));
    }
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(CustomRuntimeException::notFound);
    }

    public void update(UUID id, String content) {
        LocalDateTime validTime = LocalDateTime.now().minusMinutes(messageModifiabilityDuration);
        if (!messageRepository.isModifiable(id, validTime)) {
            throw new CustomRuntimeException("unmodifiable", HttpStatus.BAD_REQUEST);
        }
        messageRepository.updateContent(id, content);
    }

    void delete(UUID id) {
        messageRepository.deleteById(id);
    }

    Page<Message> findByConversation(UUID conversationId, int page, int size) {
        return messageRepository.findAllByToId(conversationId, PageRequest.of(page, size));
    }

    public void update(Message message) {
        messageRepository.save(message);
    }
}
