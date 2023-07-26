package vn.edu.uit.chat_application.authentication.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.repository.AttachmentRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;
import java.util.function.Supplier;

import static vn.edu.uit.chat_application.constants.Constants.UUID_LENGTH;

@RequiredArgsConstructor
@Component
public class AttachmentAuthorization {
    private final AttachmentRepository attachmentRepository;

    public AuthorizationDecision hasRightToGet(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        User user = PrincipalUtils.getLogginUserFromAuthentication(authentication.get());
        UUID userId = user.getId();
        String servletPath = context.getRequest().getServletPath();
        String idString = servletPath.substring(servletPath.length() - UUID_LENGTH);
        UUID attachmentId= UUID.fromString(idString);
        return new AuthorizationDecision(attachmentRepository.existsByIdAndMemberId(attachmentId, userId));
    }


    public AuthorizationDecision hasRightToDelete(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        User user = PrincipalUtils.getLogginUserFromAuthentication(authentication.get());
        UUID userId = user.getId();
        String servletPath = context.getRequest().getServletPath();
        String idString = servletPath.substring(servletPath.length() - UUID_LENGTH);
        UUID attachmentId= UUID.fromString(idString);
        return new AuthorizationDecision(attachmentRepository.existsByIdAndFromId(attachmentId, userId));
    }
}
