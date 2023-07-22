package vn.edu.uit.chat_application.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.received.ConversationReceivedDto;
import vn.edu.uit.chat_application.dto.sent.ConversationContentDto;
import vn.edu.uit.chat_application.dto.sent.ConversationSentDto;
import vn.edu.uit.chat_application.dto.sent.UserSentDto;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.service.ConversationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    @PutMapping
    public ResponseEntity<ConversationSentDto> createConversation(@RequestBody ConversationReceivedDto dto) {
        return ResponseEntity.ok(ConversationSentDto.from(conversationService.createConversation(dto)));
    }

    @PostMapping("/{conversationId}/members")
    public ResponseEntity<String> addMembers(@PathVariable("conversationId") UUID conversationId, @RequestBody List<UUID> userIds) {
        conversationService.addMembers(conversationId, userIds);
        return ResponseEntity.ok("update succeeded");
    }

    @DeleteMapping("/{conversationId}/members")
    public ResponseEntity<String> removeMembers(@PathVariable("conversationId") UUID conversationId, @RequestBody List<UUID> userIds) {
        conversationService.removeMembers(conversationId, userIds);
        return ResponseEntity.ok("remove succeeded");
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationSentDto> getConversation(@PathVariable("conversationId") UUID conversationId) {
        List<ConversationMembership> conversationMemberships = conversationService.getConversation(conversationId);
        ConversationSentDto result = ConversationSentDto.from(conversationMemberships.get(0).getConversation());
        result.setMembers(conversationMemberships.stream()
                .map(ConversationMembership::getMember)
                .map(UserSentDto::from)
                .toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<ConversationSentDto>> getMyConversations(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(conversationService.getMyConversations(page, size).map(ConversationSentDto::from));
    }

    @GetMapping("/{conversationId}/contents")
    public ResponseEntity<List<ConversationContentDto>> getContents(
            @PathVariable("conversationId") UUID conversationId,
            @RequestParam(value = "timestamp", required = false) Optional<LocalDateTime> optionalTimestamp,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit
    ) {
        LocalDateTime timestamp = optionalTimestamp.orElse(LocalDateTime.now());
        List<ConversationContentDto> resp = conversationService.getConversationContentsBefore(conversationId, timestamp, limit).stream()
                .map(ConversationContentDto::from)
                .toList();
        return ResponseEntity.ok(resp);
    }
}
