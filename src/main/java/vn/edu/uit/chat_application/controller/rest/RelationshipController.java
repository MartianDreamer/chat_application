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
import vn.edu.uit.chat_application.dto.sent.BlockRelationshipSentDto;
import vn.edu.uit.chat_application.dto.sent.FriendRelationshipSentDto;
import vn.edu.uit.chat_application.dto.sent.FriendRequestSentDto;
import vn.edu.uit.chat_application.entity.BlockRelationship;
import vn.edu.uit.chat_application.entity.FriendRelationship;
import vn.edu.uit.chat_application.entity.FriendRequest;
import vn.edu.uit.chat_application.entity.Notification;
import vn.edu.uit.chat_application.service.NotificationService;
import vn.edu.uit.chat_application.service.RelationshipService;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping("/rest/relationships")
@RequiredArgsConstructor
public class RelationshipController {
    private final RelationshipService relationshipService;
    private final NotificationService notificationService;

    @PutMapping("/friend-requests/{userId}")
    @Transactional
    public @ResponseBody UUID createFriendRequest(@PathVariable("userId") UUID userId) {
        FriendRequest friendRequest = relationshipService.createFriendRequest(userId);
        notificationService.sendFriendRequestNotification(friendRequest);
        return friendRequest.getId();
    }

    @PostMapping("/friend-requests/{id}")
    @Transactional
    public void acceptFriendRequest(@PathVariable("id") UUID id) {
        FriendRequest friendRequest = relationshipService.acceptFriendRequest(id);
        notificationService.sendFriendAcceptNotification(friendRequest);
        notificationService.acknowledge(friendRequest.getId(), Notification.Type.FRIEND_REQUEST);
    }

    @DeleteMapping("/friend-requests/{id}")
    public void cancelFriendRequest(@PathVariable("id") UUID id) {
        relationshipService.cancelFriendRequest(id);
    }

    @GetMapping("/friend-requests")
    public @ResponseBody Page<FriendRequestSentDto> getFriendRequests(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "from_me", required = false, defaultValue = "true") boolean fromMe
    ) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Page<FriendRequest> results;
        if (fromMe) {
            results = relationshipService.getFromFriendRequests(userId, page, size);
            return results.map(FriendRequestSentDto::friendRequestWithToUser);
        }
        results = relationshipService.getToFriendRequests(userId, page, size);
        return results.map(FriendRequestSentDto::friendRequestWithFromUser);
    }

    @DeleteMapping("/friends/{id}")
    public void unfriend(@PathVariable("id") UUID id) {
        relationshipService.unfriend(id);
    }

    @GetMapping("/friends")
    public @ResponseBody Page<FriendRelationshipSentDto> getFriends(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Page<FriendRelationship> results = relationshipService.getFriends(userId, page, size);
        Function<FriendRelationship, FriendRelationshipSentDto> converter = (e) -> FriendRelationshipSentDto.from(e, userId);
        return results.map(converter);
    }

    @PutMapping("/block")
    public @ResponseBody UUID blockUser(@RequestBody UUID userId) {
        return relationshipService.blockUser(userId).getId();
    }

    @DeleteMapping("/block/{userId}")
    public void unblockUser(@PathVariable("userId") UUID userId) {
        relationshipService.unblockUser(userId);
    }

    @GetMapping("/block")
    public @ResponseBody Page<BlockRelationshipSentDto> getBlockedRelationships(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Page<BlockRelationship> results = relationshipService.getBlockedUsers(userId, page, size);
        return results.map(BlockRelationshipSentDto::from);
    }
}
