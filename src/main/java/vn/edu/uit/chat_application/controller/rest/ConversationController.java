package vn.edu.uit.chat_application.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.received.ConversationReceivedDto;
import vn.edu.uit.chat_application.dto.sent.ConversationContentDto;
import vn.edu.uit.chat_application.dto.sent.ConversationSentDto;
import vn.edu.uit.chat_application.dto.sent.UserSentDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.Conversation;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.repository.ConversationMembershipRepository;
import vn.edu.uit.chat_application.service.ConversationService;
import vn.edu.uit.chat_application.service.NotificationService;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/conversations")
public class ConversationController {
    private final ConversationService conversationService;
    private final NotificationService notificationService;
    private final ConversationMembershipRepository conversationMembershipRepository;

    @PutMapping
    @Transactional
    public @ResponseBody ConversationSentDto createConversation(@RequestBody ConversationReceivedDto dto) {
        List<ConversationMembership> memberships = conversationService.createConversation(dto);
        notificationService.sendNewConversationNotification(memberships);
        Conversation conversation = memberships.get(0).getConversation();
        var result = ConversationSentDto.from(conversation);
        result.setMembers(memberships.stream()
                .map(ConversationMembership::getMember)
                .map(UserSentDto::from)
                .toList());
        return result;
    }

    @PostMapping("/members/{conversationId}")
    @Transactional
    public void addMembers(@PathVariable("conversationId") UUID conversationId, @RequestBody List<UUID> userIds) {
        notificationService.sendNewConversationNotification(conversationService.addMembers(conversationId, userIds));
    }

    @DeleteMapping("/members/{conversationId}")
    public void removeMembers(@PathVariable("conversationId") UUID conversationId, @RequestBody List<UUID> userIds) {
        conversationService.removeMembers(conversationId, userIds);
    }

    @GetMapping("/{conversationId}")
    public @ResponseBody ConversationSentDto getConversation(@PathVariable("conversationId") UUID conversationId) {
        List<ConversationMembership> conversationMemberships = conversationService.getConversationMembers(conversationId);
        ConversationSentDto result = ConversationSentDto.from(conversationMemberships.get(0).getConversation());
        result.setMembers(conversationMemberships.stream()
                .map(ConversationMembership::getMember)
                .map(UserSentDto::from)
                .toList());
        return result;
    }

    @PostMapping("/leave/{conversationId}")
    public void leave(@PathVariable("conversationId") UUID conversationId) {
        conversationService.removeMembers(conversationId, List.of(PrincipalUtils.getLoggedInUser().getId()));
    }

    @PutMapping("/mute/{conversationId}")
    public void mute(@PathVariable("conversationId") UUID conversationId) {
        conversationService.muteConversation(conversationId);
    }

    @DeleteMapping("/mute/{conversationId}")
    public void unmute(@PathVariable("conversationId") UUID conversationId) {
        conversationService.unmuteConversation(conversationId);
    }


    @GetMapping("/mute")
    public @ResponseBody List<ConversationSentDto> getMutedConversations() {
        return conversationService.getMyMutedConversation()
                .stream()
                .map(ConversationSentDto::from)
                .toList();
    }

    @GetMapping
    public @ResponseBody Page<ConversationSentDto> getMyConversations(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {

        Page<ConversationSentDto> conversationPage = conversationService.getMyConversations(page, size).map(ConversationSentDto::from);
        conversationPage.getContent().forEach(e -> {
            List<ConversationMembership> memberships = conversationMembershipRepository.findByConversationId(e.getId());
            e.setMembers(memberships
                    .stream()
                    .map(ConversationMembership::getMember)
                    .map(UserSentDto::from).toList()
            );
        });
        return conversationPage;
    }

    @GetMapping("/contents/{conversationId}")
    public @ResponseBody List<ConversationContentDto> getContents(
            @PathVariable("conversationId") UUID conversationId,
            @RequestParam(value = "timestamp", required = false) Optional<LocalDateTime> optionalTimestamp,
            @RequestParam(value = "limit", required = false, defaultValue = "0") int limit
    ) {
        LocalDateTime timestamp = optionalTimestamp.orElse(LocalDateTime.now());
        return conversationService.getConversationContentsBefore(conversationId, timestamp, limit).stream()
                .peek(e -> {
                    if (e instanceof Message m) {
                        notificationService.acknowledge(m.getId(), Notification.Type.MESSAGE);
                    } else if (e instanceof Attachment a) {
                        notificationService.acknowledge(a.getId(), Notification.Type.ATTACHMENT);
                    }
                })
                .map(ConversationContentDto::from)
                .toList();
    }

    @GetMapping("/latest/{conversationId}")
    public @ResponseBody List<ConversationContentDto> getContents(
            @PathVariable("conversationId") UUID conversationId
    ) {
        LocalDateTime timestamp = LocalDateTime.now();
        return conversationService.getConversationContentsBefore(conversationId, timestamp, 1).stream()
                .map(ConversationContentDto::from)
                .toList();
    }
}
