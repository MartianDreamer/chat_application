package vn.edu.uit.chat_application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.received.LoginReceivedDto;
import vn.edu.uit.chat_application.dto.sent.TokenDto;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.service.AuthenticationService;
import vn.edu.uit.chat_application.service.JwtService;

@RestController
@RequestMapping("/rest/login")
@RequiredArgsConstructor
public class LoginController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<TokenDto> getToken(@RequestBody LoginReceivedDto dto) {
        return authenticationService
                .authenticate(dto)
                .map(jwtService::issueTokenPair)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomRuntimeException("username or password is wrong", HttpStatus.FORBIDDEN));
    }
}
