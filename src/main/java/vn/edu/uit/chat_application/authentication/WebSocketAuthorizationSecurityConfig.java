package vn.edu.uit.chat_application.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;


@Configuration
public class WebSocketAuthorizationSecurityConfig {
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages = MessageMatcherDelegatingAuthorizationManager.builder();
        return messages
                .anyMessage()
                .authenticated()
                .build();
    }
}
