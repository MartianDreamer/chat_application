package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.received.MessageReceivedDto;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.repository.MessageRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    Message save(MessageReceivedDto dto) {
        return messageRepository.save(Message.from(dto));
    }

    void delete(UUID id) {
        messageRepository.deleteById(id);
    }

    Page<Message> findByConversation(UUID conversationId, int page, int size) {
        return messageRepository.findAllByConversationId(conversationId, PageRequest.of(page, size));
    }
}
