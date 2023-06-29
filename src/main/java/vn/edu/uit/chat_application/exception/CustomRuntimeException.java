package vn.edu.uit.chat_application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomRuntimeException extends RuntimeException {
    private final HttpStatus httpResponseStatus;
    public CustomRuntimeException(String message, HttpStatus httpResponseStatus) {
        super(message);
        this.httpResponseStatus = httpResponseStatus;
    }
    public static CustomRuntimeException notFound() {
        return new CustomRuntimeException("resource not found", HttpStatus.NOT_FOUND);
    }
}
