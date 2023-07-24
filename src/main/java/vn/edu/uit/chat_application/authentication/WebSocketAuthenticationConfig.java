package vn.edu.uit.chat_application.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketJwtChannelInterceptor webSocketJwtChannelInterceptor;
    private final AuthorizationManager<Message<?>> authorizationManager;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationChannelInterceptor authorizationChannelInterceptor = new AuthorizationChannelInterceptor(authorizationManager);
        AuthorizationEventPublisher authorizationEventPublisher = new SpringAuthorizationEventPublisher(applicationEventPublisher);
        authorizationChannelInterceptor.setAuthorizationEventPublisher(authorizationEventPublisher);
        registration.interceptors(webSocketJwtChannelInterceptor);
        registration.interceptors(new SecurityContextChannelInterceptor(), authorizationChannelInterceptor);
    }
}
