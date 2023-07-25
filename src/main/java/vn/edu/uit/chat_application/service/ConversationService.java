package vn.edu.uit.chat_application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.received.ConversationReceivedDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.Conversation;
import vn.edu.uit.chat_application.entity.Conversation.ConversationBuilder;
import vn.edu.uit.chat_application.entity.ConversationContent;
import vn.edu.uit.chat_application.entity.ConversationMembership;
import vn.edu.uit.chat_application.entity.Message;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.ConversationMembershipRepository;
import vn.edu.uit.chat_application.repository.ConversationRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMembershipRepository conversationMembershipRepository;
    private final RelationshipService relationshipService;
    private final EntityManager entityManager;

    public Conversation createConversation(ConversationReceivedDto dto) {
        User creator = PrincipalUtils.getLoggedInUser();
        UUID creatorId = creator.getId();
        dto.getMembers().stream()
                .filter(e -> relationshipService.areNotFriends(creatorId, e))
                .findFirst()
                .ifPresent((e) -> {
                    throw new CustomRuntimeException("the person with this id " + e + " is not your friend ", HttpStatus.BAD_REQUEST);
                });
        ConversationBuilder conversationBuilder = Conversation.builder()
                .createdAt(LocalDateTime.now())
                .name(dto.getName());
        if (dto.getMembers().size() <= 1) {
            Set<UUID> memberSet = new HashSet<>();
            memberSet.add(creatorId);
            memberSet.addAll(dto.getMembers());
            String duplicatedTwoPeopleConversationIdentifier = memberSet.stream().sorted(Comparator.naturalOrder()).map(UUID::toString).collect(Collectors.joining("_"));
            if (conversationRepository.existsByDuplicatedTwoPeopleConversationIdentifier(duplicatedTwoPeopleConversationIdentifier)) {
                throw new CustomRuntimeException("conversation is existed", HttpStatus.BAD_REQUEST);
            }
            conversationBuilder.duplicatedTwoPeopleConversationIdentifier(duplicatedTwoPeopleConversationIdentifier);
        }
        Conversation savedConversation = conversationRepository.save(conversationBuilder.build());
        List<ConversationMembership> conversationMemberships = dto.getMembers().stream()
                .map(User::new)
                .map(e -> new ConversationMembership(savedConversation, e))
                .collect(Collectors.toCollection(LinkedList::new));
        conversationMemberships.add(new ConversationMembership(savedConversation, creator));
        conversationMembershipRepository.saveAll(conversationMemberships);
        return savedConversation;
    }


    public void addMembers(UUID conversationId, List<UUID> memberIds) {
        UUID adderId = PrincipalUtils.getLoggedInUser().getId();
        if (memberIds.stream().anyMatch(e -> relationshipService.areNotFriends(adderId, e))) {
            throw new CustomRuntimeException("the person whom you added into this conversation is not your friend", HttpStatus.BAD_REQUEST);
        }
        if (memberIds.stream().anyMatch(e -> isMember(conversationId, e))) {
            throw new CustomRuntimeException("this person is already a member", HttpStatus.BAD_REQUEST);
        }
        List<ConversationMembership> savedMemberships = memberIds.stream().map(e -> new ConversationMembership(new Conversation(conversationId), new User(e))).toList();
        conversationMembershipRepository.saveAll(savedMemberships);
    }

    public void removeMembers(UUID conversationId, List<UUID> memberIds) {
        memberIds.stream()
                .filter(e -> isMember(conversationId, e))
                .findFirst()
                .ifPresent((e) -> {
                    throw new CustomRuntimeException("the person with this id " + e + " is not a member of this conversation", HttpStatus.BAD_REQUEST);
                });
        conversationMembershipRepository.deleteByConversationIdAndMemberIdIn(conversationId, memberIds);
    }

    public List<ConversationMembership> getConversationMembers(UUID conversationId) {
        List<ConversationMembership> results = conversationMembershipRepository.findByConversationId(conversationId);
        if (results.isEmpty()) {
            throw CustomRuntimeException.notFound();
        }
        return results;
    }

    public Page<Conversation> getMyConversations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        UUID myId = PrincipalUtils.getLoggedInUser().getId();
        return conversationMembershipRepository.findByMemberId(myId, pageable).map(ConversationMembership::getConversation);
    }

    public List<ConversationContent> getConversationContentsBefore(UUID conversationId, LocalDateTime before, int limit) {
        Stream<ConversationContent> contents = Stream.of(getConversationContentsBefore(conversationId, before, limit, Message.class),
                        getConversationContentsBefore(conversationId, before, limit, Attachment.class))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(ConversationContent::getTimestamp));
        if (limit > 0) {
            contents = contents.limit(limit);
        }
        return contents.toList();
    }

    private <T extends ConversationContent> List<ConversationContent> getConversationContentsBefore(UUID conversationId, LocalDateTime before, int limit, Class<T> clazz) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ConversationContent> criteriaQuery = criteriaBuilder.createQuery(ConversationContent.class);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root)
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), before),
                                criteriaBuilder.equal(root.get("to").get("id"), conversationId)
                        )
                )
                .orderBy(criteriaBuilder.desc(root.get("timestamp")));
        TypedQuery<ConversationContent> query = entityManager.createQuery(criteriaQuery);
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query
                .getResultList();
    }

    public boolean isMember(UUID conversationId, UUID userId) {
        return conversationMembershipRepository.existsByConversationIdAndMemberId(conversationId, userId);
    }
}
