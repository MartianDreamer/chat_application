package vn.edu.uit.chat_application.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import vn.edu.uit.chat_application.constants.Role;

@Configuration
@EnableWebSocketSecurity
public class WebSocketAuthorizationSecurityConfig {
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages
                .simpMessageDestMatchers("/app/admin")
                .hasAuthority(Role.ADMIN.getAuthority())
                .anyMessage()
                .authenticated()
                .build();
    }
}
