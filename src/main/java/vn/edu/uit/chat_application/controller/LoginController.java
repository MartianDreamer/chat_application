package vn.edu.uit.chat_application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.received.LoginReceivedDto;
import vn.edu.uit.chat_application.dto.sent.TokenSentDto;
import vn.edu.uit.chat_application.service.JwtService;
import vn.edu.uit.chat_application.service.UserService;

@RestController
@RequestMapping("/rest/login")
@RequiredArgsConstructor
public class LoginController {
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TokenSentDto> getToken(@RequestBody LoginReceivedDto dto) {
        return userService
                .authenticate(dto)
                .map(e -> ResponseEntity.ok(jwtService.issueTokenPair(e)))
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
