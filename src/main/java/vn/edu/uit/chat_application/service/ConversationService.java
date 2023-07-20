package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.entity.Conversation;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.ConversationMembershipRepository;
import vn.edu.uit.chat_application.repository.ConversationRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static vn.edu.uit.chat_application.entity.Conversation.ConversationBuilder;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMembershipRepository conversationMembershipRepository;
    private final RelationshipService relationshipService;

    public Conversation createConversation(String name, List<UUID> members) {
        UUID creatorId = PrincipalUtils.getLoggedInUser().getId();
        if (members.stream()
                .anyMatch(e -> !relationshipService.areFriends(creatorId, e))) {
            throw new CustomRuntimeException("people whom you added into this conversation are not your friend", HttpStatus.BAD_REQUEST);
        }
        ConversationBuilder conversationBuilder = Conversation.builder()
                .createdAt(LocalDateTime.now())
                .name(name);
        if (members.size() == 1) {
            List<UUID> memberList = new ArrayList<>(List.of(creatorId, members.get(0)));
            memberList.sort(Comparator.naturalOrder());
            String duplicatedTwoPeopleConversationIdentifier = memberList.toString();
            if (conversationRepository.existsByDuplicatedTwoPeopleConversationIdentifier(duplicatedTwoPeopleConversationIdentifier)) {
                throw new CustomRuntimeException("conversation is existed", HttpStatus.BAD_REQUEST);
            }
            conversationBuilder.duplicatedTwoPeopleConversationIdentifier(duplicatedTwoPeopleConversationIdentifier);
        }
        Conversation savedConversation = conversationRepository.save(conversationBuilder.build());
        List<ConversationMembership> conversationMemberships = members.stream()
                .map(e -> User.builder().id(e).build())
                .map(e -> new ConversationMembership(e, savedConversation))
                .toList();
        conversationMembershipRepository.saveAll(conversationMemberships);
        return savedConversation;
    }

    public void addMember(UUID conversationId, UUID memberId) {

    }
}
