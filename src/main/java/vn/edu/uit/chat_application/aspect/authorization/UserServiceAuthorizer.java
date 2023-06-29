package vn.edu.uit.chat_application.aspect.authorization;

import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.dto.received.UserReceivedDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.util.PrincipalUtils;

@Component
public class UserServiceAuthorizer implements Authorizer {
    @Override
    public boolean authorize(MethodType methodType, Object[] args, Object returnValue) {
        return switch (methodType) {
            case SINGLE_READ -> authorizeSingleRead();
            case SINGLE_WRITE -> authorizeSingleWrite(args);
            case MULTIPLE_READ -> authorizeMultipleRead();
            case MULTIPLE_WRITE -> authorizeMultipleWrite();
            case UNIDENTIFIED -> true;
        };
    }

    private boolean authorizeSingleWrite(Object[] args) {
        UserReceivedDto dto = (UserReceivedDto) args[0];
        User loggedInUser = PrincipalUtils.getLoggedInUser();
        return dto.getId() == null || dto.getId().equals(loggedInUser.getId());
    }

    private boolean authorizeSingleRead() {
        return true;
    }

    private boolean authorizeMultipleWrite() {
        return false;
    }

    private boolean authorizeMultipleRead() {
        return true;
    }
}
