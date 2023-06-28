package vn.edu.uit.chat_application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.UserReceivedDto;
import vn.edu.uit.chat_application.dto.UserSentDto;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/rest/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<UserSentDto> createOrUpdateUser(@RequestBody UserReceivedDto dto) {
        return ResponseEntity.ok(UserSentDto.from(userService.saveUser(dto)));
    }

    @PostMapping("/confirm/{confirmationString}")
    public ResponseEntity<String> activateUser(@PathVariable("confirmationString") String confirmationString) {
        if (userService.activateUser(confirmationString)) {
            return ResponseEntity.ok().build();
        }
        throw new CustomRuntimeException("invalid confirmation", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserSentDto> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(UserSentDto.from(userService.loadByUsername(username)));
    }

    @GetMapping("/confirmation/reset/{id}")
    public ResponseEntity<String> resetConfirmationString(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.resetConfirmationString(id));
    }
}
