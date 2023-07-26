package vn.edu.uit.chat_application.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.chat_application.dto.received.UserReceivedDto;
import vn.edu.uit.chat_application.dto.sent.AttachmentContentDto;
import vn.edu.uit.chat_application.dto.sent.UserSentDto;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.service.UserService;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.UUID;

@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping
    public @ResponseBody String createUser(@RequestBody UserReceivedDto dto) {
        return userService.createUser(dto);
    }

    @PatchMapping("/{id}")
    public @ResponseBody String updateUser(@PathVariable("id") UUID id, @RequestBody UserReceivedDto dto) {
        userService.updateUser(id, dto);
        return "updated";
    }

    @PostMapping("/confirm/{confirmationString}")
    public void activateUser(@PathVariable("confirmationString") String confirmationString) {
        if (!userService.activateUser(confirmationString)) {
            throw new CustomRuntimeException("invalid confirmation", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public @ResponseBody UserSentDto findByUsername(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "self", required = false, defaultValue = "false") boolean self) {
        if (self) {
            return UserSentDto.from(PrincipalUtils.getLoggedInUser());
        }
        return UserSentDto.from(userService.loadByUsername(username));
    }

    @GetMapping("/{id}")
    public @ResponseBody UserSentDto findById(@PathVariable("id") UUID username) {
        return UserSentDto.from(userService.findById(username).orElseThrow(CustomRuntimeException::notFound));
    }

    @PostMapping("/avatar/{id}")
    public void uploadAvatar(@PathVariable("id") UUID id, @RequestParam("file")MultipartFile file) {
        userService.uploadAvatar(id, file);
    }

    @GetMapping("/avatar/{id}")
    public @ResponseBody AttachmentContentDto loadAvatar(@PathVariable("id") UUID id) {
        return userService.loadAvatar(id);
    }
}
