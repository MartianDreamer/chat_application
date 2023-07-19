package vn.edu.uit.chat_application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.received.UserReceivedDto;
import vn.edu.uit.chat_application.dto.sent.UserSentDto;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<String> createUser(@RequestBody UserReceivedDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable("id") UUID id, @RequestBody UserReceivedDto dto) {
        userService.updateUser(id, dto);
        return ResponseEntity.ok("updated");
    }

    @PostMapping("/confirm/{confirmationString}")
    public ResponseEntity<Void> activateUser(@PathVariable("confirmationString") String confirmationString) {
        if (userService.activateUser(confirmationString)) {
            return ResponseEntity.ok().build();
        }
        throw new CustomRuntimeException("invalid confirmation", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserSentDto> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(UserSentDto.from(userService.loadByUsername(username)));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserSentDto> findById(@PathVariable("id") UUID username) {
        return ResponseEntity.ok(UserSentDto.from(userService.findById(username)));
    }
}
