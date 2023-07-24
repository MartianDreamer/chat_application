package vn.edu.uit.chat_application.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @PostMapping(consumes = {MediaType.ALL_VALUE})
    public @ResponseBody TokenDto getToken(@RequestBody LoginReceivedDto dto) {
        return authenticationService
                .authenticate(dto)
                .map(jwtService::issueTokenPair)
                .orElseThrow(() -> new CustomRuntimeException("username or password is wrong", HttpStatus.FORBIDDEN));
    }
}
