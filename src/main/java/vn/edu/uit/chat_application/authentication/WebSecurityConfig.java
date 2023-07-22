package vn.edu.uit.chat_application.authentication;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import vn.edu.uit.chat_application.authentication.authorization.RelationshipControllerAuthorization;
import vn.edu.uit.chat_application.authentication.authorization.UserControllerAuthorization;
import vn.edu.uit.chat_application.service.UserService;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final HttpJwtFilter httpJwtFilter;
    private final UserService userService;
    private final UserControllerAuthorization userControllerAuthorization;
    private final RelationshipControllerAuthorization relationshipControllerAuthorization;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
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
    @SneakyThrows
    public SecurityFilterChain config(HttpSecurity httpSecurity) {
        return httpSecurity
                .addFilterBefore(httpJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    RequestMatcher[] permitAllRequests = {
                            RegexRequestMatcher.regexMatcher(HttpMethod.PUT, "/rest/users"),
                            RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/rest/users/confirm.*"),
                            RegexRequestMatcher.regexMatcher("/swagger-ui.html.*"),
                            RegexRequestMatcher.regexMatcher("/v3/api-docs.*"),
                            RegexRequestMatcher.regexMatcher("/swagger-ui.*"),
                            RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/rest/login")
                    };
                    authorizationManagerRequestMatcherRegistry
                            .requestMatchers(permitAllRequests)
                            .permitAll()
                            // config authorization for UserController
                            .requestMatchers(
                                    RegexRequestMatcher.regexMatcher(HttpMethod.PATCH, "/rest/users/.{36}")
                            )
                            .access(userControllerAuthorization::update)
                            // config authorization for RelationshipController
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.DELETE, "/rest/relationships/friend-requests/.{36}"))
                            .access(relationshipControllerAuthorization::cancelFriendRequest)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/rest/relationships/friend-requests/.{36}"))
                            .access(relationshipControllerAuthorization::acceptFriendRequest)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.DELETE, "/rest/relationships/friends/.{36}"))
                            .access(relationshipControllerAuthorization::unfriend)
                            .anyRequest()
                            .authenticated();
                })
                .build();
    }

}
