package vn.edu.uit.chat_application.constants;

import org.springframework.beans.factory.annotation.Value;

public final class Constants {
    private Constants() {
        super();
    }

    @Value("${app.confirm-duration-in-day}")
    public static int CONFIRMATION_DURATION_IN_DAY;
    public static int UUID_LENGTH = 36;
    public static int MAX_AVATAR_SIZE = 3 * 1024 * 1024;
    public static int MAX_ATTACHMENT_SIZE = 30*1024*1024;
}
