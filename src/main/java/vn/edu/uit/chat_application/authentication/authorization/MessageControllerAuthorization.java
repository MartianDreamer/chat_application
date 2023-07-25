package vn.edu.uit.chat_application.authentication.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.service.ConversationService;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;
import java.util.function.Supplier;

import static vn.edu.uit.chat_application.constants.Constants.UUID_LENGTH;

@RequiredArgsConstructor
@Component
public class MessageControllerAuthorization {
    private final ConversationService conversationService;
    public AuthorizationDecision isMember(Supplier<Authentication> authentication, MessageAuthorizationContext<?> object) {
        UUID userId = PrincipalUtils.getLogginUserFromAuthentication(authentication.get()).getId();
        String destination = object.getMessage().getHeaders().get("simpDestination", String.class);
        if (destination == null) {
            throw new CustomRuntimeException("destination is null", HttpStatus.BAD_REQUEST);
        }
        UUID conversationId = UUID.fromString(destination.substring(destination.length() - UUID_LENGTH));
        return new AuthorizationDecision(conversationService.isMember(conversationId, userId));
    }
}
