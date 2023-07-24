package vn.edu.uit.chat_application.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<String> internalException() {
        return ResponseEntity.internalServerError().body("internal");
    }

    @ExceptionHandler(value = {CustomRuntimeException.class})
    public ResponseEntity<String> handleCustomRuntimeException(CustomRuntimeException e) {
        return ResponseEntity.status(e.getHttpResponseStatus()).body(e.getMessage());
    }
}
