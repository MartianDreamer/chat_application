package vn.edu.uit.chat_application.authentication.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.entity.FriendRelationship;
import vn.edu.uit.chat_application.repository.FriendRelationshipRepository;
import vn.edu.uit.chat_application.repository.FriendRequestRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static vn.edu.uit.chat_application.constants.Constants.UUID_LENGTH;

@Component
@RequiredArgsConstructor
public class RelationshipControllerAuthorization {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRelationshipRepository friendRelationshipRepository;

    public AuthorizationDecision acceptFriendRequest(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String servletPath = context.getRequest().getServletPath();
        String idString = servletPath.substring(servletPath.length() - UUID_LENGTH);
        UUID friendRequestId = UUID.fromString(idString);
        UUID toId = PrincipalUtils.getLogginUserFromAuthentication(authentication.get()).getId();
        return new AuthorizationDecision(friendRequestRepository.existsByIdAndToId(friendRequestId, toId));
    }

    public AuthorizationDecision cancelFriendRequest(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String servletPath = context.getRequest().getServletPath();
        String idString = servletPath.substring(servletPath.length() - UUID_LENGTH);
        UUID friendRequestId = UUID.fromString(idString);
        UUID fromOrToId = PrincipalUtils.getLogginUserFromAuthentication(authentication.get()).getId();
        return new AuthorizationDecision(friendRequestRepository.existsByIdAndToIdOrFromId(friendRequestId, fromOrToId));
    }

    public AuthorizationDecision unfriend(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String servletPath = context.getRequest().getServletPath();
        String idString = servletPath.substring(servletPath.length() - UUID_LENGTH);
        UUID friendRelationshipId = UUID.fromString(idString);
        UUID loggedInUserId = PrincipalUtils.getLogginUserFromAuthentication(authentication.get()).getId();
        Optional<FriendRelationship> optionalFriendRelationship = friendRelationshipRepository.findById(friendRelationshipId);
        if (optionalFriendRelationship.isPresent()) {
            UUID firstId = optionalFriendRelationship.get().getFirst().getId();
            UUID secondId = optionalFriendRelationship.get().getSecond().getId();
            return new AuthorizationDecision(loggedInUserId.equals(firstId) || loggedInUserId.equals(secondId));
        }
        return new AuthorizationDecision(false);
    }
}
