package vn.edu.uit.chat_application.authentication.authorization;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.FriendRequestRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class RelationshipServiceAuthorization {

    private final FriendRequestRepository friendRequestRepository;

    @Before(value = "execution(public void vn.edu.uit.chat_application.service.RelationshipService.acceptFriendRequest(..))")
    public void acceptFriendRequest(JoinPoint joinPoint) {
        UUID friendRequestId = (UUID) joinPoint.getArgs()[0];
        UUID toId = PrincipalUtils.getLoggedInUser().getId();
        if (!friendRequestRepository.existsByIdAndToId(friendRequestId, toId)) {
            throw new CustomRuntimeException("this friend request is not to you", HttpStatus.FORBIDDEN);
        }
    }

    @Before(value = "execution(public void vn.edu.uit.chat_application.service.RelationshipService.cancelFriendRequest(..))")
    public void cancelFriendRequest(JoinPoint joinPoint) {
        UUID friendRequestId = (UUID) joinPoint.getArgs()[0];
        UUID fromOrToId = PrincipalUtils.getLoggedInUser().getId();
        if (!friendRequestRepository.existsByIdAndToIdOrFromId(friendRequestId, fromOrToId)) {
            throw new CustomRuntimeException("this friend request is not to you", HttpStatus.FORBIDDEN);
        }
    }
}
