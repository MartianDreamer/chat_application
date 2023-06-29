package vn.edu.uit.chat_application.aspect.authorization;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.controller.UserController;
import vn.edu.uit.chat_application.dto.UserReceivedDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.util.PrincipalUtils;

@Aspect
@Component
public class UserApisAuthorizeProcessor extends AuthorizationProcessor {
    public UserApisAuthorizeProcessor() {
        super(UserController.class);
    }

    @After(value = "@within(vn.edu.uit.chat_application.aspect.annotation.UserApisAuthorize) || " +
            "@annotation(vn.edu.uit.chat_application.aspect.annotation.UserApisAuthorize)")
    public void authorize(JoinPoint joinPoint) {
        super.authorize(joinPoint);
    }

    public void updateUser(JoinPoint joinPoint) {
        UserReceivedDto userReceivedDto = (UserReceivedDto) joinPoint.getArgs()[0];
        User loggedInUser = PrincipalUtils.getLoggedInUser();
        if (!loggedInUser.getId().equals(userReceivedDto.getId())) {
            throw new CustomRuntimeException("forbidden", HttpStatus.FORBIDDEN);
        }
    }
}
