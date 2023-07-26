package vn.edu.uit.chat_application.authentication.authorization;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;
import java.util.function.Supplier;
import static vn.edu.uit.chat_application.constants.Constants.UUID_LENGTH;

@Component
public class UserAuthorization {
    public AuthorizationDecision update(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String servletPath = context.getRequest().getServletPath();
        String idString = servletPath.substring(servletPath.length() - UUID_LENGTH);
        UUID userId = UUID.fromString(idString);
        User loggedInUser = PrincipalUtils.getLogginUserFromAuthentication(authentication.get());
        return new AuthorizationDecision(loggedInUser.getId().equals(userId));
    }
}
