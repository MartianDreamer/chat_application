package vn.edu.uit.chat_application.aspect.authorization;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;
import java.util.stream.Stream;

@Aspect
@Component
public class UserServiceAuthorization {
    @Before(value = "execution(public void vn.edu.uit.chat_application.service.UserService.updateUser(..))")
    public void updateUser(JoinPoint joinPoint) {
        Stream.of(joinPoint.getArgs())
                .filter(e -> e instanceof UUID)
                .map(UUID.class::cast)
                .filter(e -> PrincipalUtils.getLoggedInUser().getId().equals(e))
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException("forbidden", HttpStatus.FORBIDDEN));
    }
}
