package vn.edu.uit.chat_application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomRuntimeException extends RuntimeException {
    private final HttpStatus httpResponseStatus;

    public CustomRuntimeException(HttpStatus httpResponseStatus) {
        super();
        this.httpResponseStatus = httpResponseStatus;
    }

    public CustomRuntimeException(String message, HttpStatus httpResponseStatus) {
        super(message);
        this.httpResponseStatus = httpResponseStatus;
    }

    public CustomRuntimeException(String message, Throwable cause, HttpStatus httpResponseStatus) {
        super(message, cause);
        this.httpResponseStatus = httpResponseStatus;
    }

    public CustomRuntimeException(Throwable cause, HttpStatus httpResponseStatus) {
        super(cause);
        this.httpResponseStatus = httpResponseStatus;
    }

    protected CustomRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus httpResponseStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.httpResponseStatus = httpResponseStatus;
    }

}
