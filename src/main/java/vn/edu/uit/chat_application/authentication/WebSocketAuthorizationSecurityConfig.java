package vn.edu.uit.chat_application.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import vn.edu.uit.chat_application.authentication.authorization.MessageControllerAuthorization;


@Configuration
@RequiredArgsConstructor
public class WebSocketAuthorizationSecurityConfig {
    private final MessageControllerAuthorization messageControllerAuthorization;

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages = MessageMatcherDelegatingAuthorizationManager.builder();
        return messages
                .simpMessageDestMatchers("/app/conversations/*")
                .access(messageControllerAuthorization::isMember)
                .anyMessage()
                .authenticated()
                .build();
    }
}
