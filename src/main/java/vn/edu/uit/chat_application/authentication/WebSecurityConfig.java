package vn.edu.uit.chat_application.authentication;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.edu.uit.chat_application.authentication.authorization.AttachmentAuthorization;
import vn.edu.uit.chat_application.authentication.authorization.ConversationAuthorization;
import vn.edu.uit.chat_application.authentication.authorization.RelationshipAuthorization;
import vn.edu.uit.chat_application.filter.CustomWsCorsFilter;
import vn.edu.uit.chat_application.filter.HttpJwtFilter;
import vn.edu.uit.chat_application.service.UserService;

import java.util.List;

@Configuration
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final HttpJwtFilter httpJwtFilter;
    private final UserService userService;
    private final RelationshipAuthorization relationshipAuthorization;
    private final ConversationAuthorization conversationAuthorization;
    private final AttachmentAuthorization attachmentAuthorization;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", configuration);
        return configurationSource;
    }

    @Bean
    @SneakyThrows
    @Autowired
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder) {
        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Autowired
    public FilterRegistrationBean<CustomWsCorsFilter> customWsCorsFilterFilterRegistrationBean(CustomWsCorsFilter customWsCorsFilter) {
        FilterRegistrationBean<CustomWsCorsFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(customWsCorsFilter);
        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        filterRegistrationBean.addUrlPatterns("/ws/*");
        return filterRegistrationBean;
    }

    @Bean
    @SneakyThrows
    public SecurityFilterChain config(HttpSecurity httpSecurity) {
        return httpSecurity
                .addFilterBefore(httpJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    RequestMatcher[] permitAllRequests = {
                            RegexRequestMatcher.regexMatcher("/ws/.*"),
                            RegexRequestMatcher.regexMatcher(HttpMethod.PUT, "/rest/users"),
                            RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/rest/users/confirm.*"),
                            RegexRequestMatcher.regexMatcher("/swagger-ui.html.*"),
                            RegexRequestMatcher.regexMatcher("/v3/api-docs.*"),
                            RegexRequestMatcher.regexMatcher("/swagger-ui.*"),
                            RegexRequestMatcher.regexMatcher("/rest/login")
                    };
                    authorizationManagerRequestMatcherRegistry
                            .requestMatchers(permitAllRequests)
                            .permitAll()
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.DELETE, "/rest/relationships/friend-requests/.{36}"))
                            .access(relationshipAuthorization::cancelFriendRequest)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/rest/relationships/friend-requests/.{36}"))
                            .access(relationshipAuthorization::acceptFriendRequest)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.DELETE, "/rest/relationships/friends/.{36}"))
                            .access(relationshipAuthorization::unfriend)
                            .requestMatchers(RegexRequestMatcher.regexMatcher("/rest/conversations/.*.{36}$"))
                            .access(conversationAuthorization::isMember)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST,"/rest/attachments/.{36}"))
                            .access(conversationAuthorization::isMember)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/rest/attachments/.{36}"))
                            .access(attachmentAuthorization::hasRightToGet)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.DELETE, "/rest/attachments/.{36}"))
                            .access(attachmentAuthorization::hasRightToDelete)
                            .anyRequest()
                            .authenticated();
                })
                .build();
    }

}
