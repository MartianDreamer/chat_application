package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import vn.edu.uit.chat_application.entity.BlockRelationship;
import vn.edu.uit.chat_application.entity.FriendRelationship;
import vn.edu.uit.chat_application.entity.FriendRequest;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.BlockRelationshipRepository;
import vn.edu.uit.chat_application.repository.FriendRelationshipRepository;
import vn.edu.uit.chat_application.repository.FriendRequestRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;

@RequiredArgsConstructor
public class RelationshipService {
    private final FriendRelationshipRepository friendRelationshipRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final BlockRelationshipRepository blockRelationshipRepository;
    private final UserService userService;

    public FriendRequest createFriendRequest(String username) {
        User creator = PrincipalUtils.getLoggedInUser();
        User user = userService.loadByUsername(username);
        if (blockRelationshipRepository.existsByUserIds(creator.getId(), user.getId())) {
            throw new CustomRuntimeException("blocked", HttpStatus.BAD_REQUEST);
        } else if (friendRelationshipRepository.existsByUserIds(creator.getId(), user.getId())) {
            throw new CustomRuntimeException("you and " + username + " are friend already", HttpStatus.BAD_REQUEST);
        } else if (friendRequestRepository.existsByUserIds(creator.getId(), user.getId())) {
            throw new CustomRuntimeException("friend request existed", HttpStatus.BAD_REQUEST);
        }
        return friendRequestRepository.save(new FriendRequest(creator, user));
    }

    public void cancelFriendRequest(UUID id) {
        friendRequestRepository.deleteById(id);
    }

    public void acceptFriendRequest(UUID id) {
        FriendRequest friendRequest = friendRequestRepository.findById(id).orElseThrow(CustomRuntimeException::notFound);
        friendRelationshipRepository.save(new FriendRelationship(friendRequest.getFrom(), friendRequest.getTo()));
        friendRequestRepository.deleteById(id);
    }

    public Page<FriendRequest> findFromFriendRequests(int page, int size) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        return friendRequestRepository.findAllByFromId(userId, PageRequest.of(page, size));
    }

    public Page<FriendRequest> findToFriendRequests(int page, int size) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        return friendRequestRepository.findAllByToId(userId, PageRequest.of(page, size));
    }

    public Page<FriendRelationship> findFriends(int page, int size) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        return friendRelationshipRepository.findAllByFirstIdOrSecondId(userId, PageRequest.of(page, size));
    }

    public Page<BlockRelationship> findBlockeds(int page, int size) {
        UUID userId = PrincipalUtils.getLoggedInUser().getId();
        return blockRelationshipRepository.findAllByBlockerId(userId, PageRequest.of(page, size));
    }

    public void blockUser(UUID userId) {
        User blocker = PrincipalUtils.getLoggedInUser();
        User blocked = User.builder().id(userId).build();
        blockRelationshipRepository.save(new BlockRelationship(blocker, blocked));
    }

    public void unfriend(UUID id) {
        friendRelationshipRepository.deleteById(id);
    }

    public void unblockUser(UUID id) {
        blockRelationshipRepository.deleteById(id);
    }
}
