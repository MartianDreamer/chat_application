package vn.edu.uit.chat_application.util;

import org.springframework.security.core.context.SecurityContextHolder;
import vn.edu.uit.chat_application.entity.User;

public final class PrincipalUtils {
    private PrincipalUtils() {
        super();
    }

    public static User getLoggedInUser() {
        Object principals = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principals instanceof User user)
            return user;
        throw new ClassCastException();
    }
}
