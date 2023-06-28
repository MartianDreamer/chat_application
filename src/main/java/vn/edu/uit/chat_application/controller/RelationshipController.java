package vn.edu.uit.chat_application.controller;

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
import vn.edu.uit.chat_application.dto.BlockRelationshipSentDto;
import vn.edu.uit.chat_application.dto.FriendRelationshipSentDto;
import vn.edu.uit.chat_application.dto.FriendRequestSentDto;
import vn.edu.uit.chat_application.entity.BlockRelationship;
import vn.edu.uit.chat_application.entity.FriendRelationship;
import vn.edu.uit.chat_application.entity.FriendRequest;
import vn.edu.uit.chat_application.service.RelationshipService;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping("/rest/relationships")
@RequiredArgsConstructor
public class RelationshipController {
    private final RelationshipService relationshipService;

    @PutMapping("/friend-requests")
    public ResponseEntity<UUID> createFriendRequest(@RequestBody UUID userId) {
        return ResponseEntity.ok(relationshipService.createFriendRequest(userId).getId());
    }

    @PostMapping("/friend-requests/{id}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable("id") UUID id) {
        relationshipService.acceptFriendRequest(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/friend-requests/{id}")
    public ResponseEntity<Void> cancelFriendRequest(@PathVariable("id") UUID id) {
        relationshipService.cancelFriendRequest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friend-requests/from")
    public ResponseEntity<Page<FriendRequestSentDto>> getFromFriendRequest(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Page<FriendRequest> results = relationshipService.getFromFriendRequests(userId, page, size);
        return ResponseEntity.ok(results.map(FriendRequestSentDto::fromFrom));
    }

    @GetMapping("/friend-requests/to")
    public ResponseEntity<Page<FriendRequestSentDto>> getToFriendRequest(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Page<FriendRequest> results = relationshipService.getToFriendRequests(userId, page, size);
        return ResponseEntity.ok(results.map(FriendRequestSentDto::fromTo));
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<Void> unfriend(@PathVariable("id") UUID id) {
        relationshipService.unfriend(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friends")
    public ResponseEntity<Page<FriendRelationshipSentDto>> getFriends(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Page<FriendRelationship> results = relationshipService.getFriends(userId, page, size);
        Function<FriendRelationship, FriendRelationshipSentDto> converter = (e) -> FriendRelationshipSentDto.from(e, userId);
        return ResponseEntity.ok(results.map(converter));
    }

    @PutMapping("/block")
    public ResponseEntity<UUID> blockUser(@RequestBody UUID userId) {
        return ResponseEntity.ok(relationshipService.blockUser(userId).getId());
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<Void> unblockUser(@PathVariable("id") UUID id) {
        relationshipService.unblockUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/block")
    public ResponseEntity<Page<BlockRelationshipSentDto>> getBlockedUsers(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        Page<BlockRelationship> results = relationshipService.getBlockedUsers(userId, page, size);
        return ResponseEntity.ok(results.map(BlockRelationshipSentDto::from));
    }
}
