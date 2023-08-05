package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.chat_application.aspect.annotation.EncryptPassword;
import vn.edu.uit.chat_application.dto.received.UserReceivedDto;
import vn.edu.uit.chat_application.dto.sent.AttachmentContentDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.UserRepository;
import vn.edu.uit.chat_application.util.CommonUtils;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static vn.edu.uit.chat_application.constants.Constants.MAX_AVATAR_SIZE;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StorageService storageService;
    private static final String AVATAR_PREFIX = "/avatar";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @EncryptPassword
    public User createUser(UserReceivedDto dto) {
        return userRepository.saveAndFlush(User.from(dto));
    }

    @EncryptPassword
    public void updateUser(UUID id, UserReceivedDto dto) {
        userRepository.findById(id).ifPresentOrElse(o -> {
            CommonUtils.copyPropertiesIgnoreNull(dto, o);
            userRepository.save(o);
        }, () -> {
            throw CustomRuntimeException.notFound();
        });
    }

    public User loadByUsername(String username) {
        User user = userRepository.findByUsernameAndNotBlocked(username, PrincipalUtils.getLoggedInUser().getId());
        if (user == null) {
            throw new CustomRuntimeException("user not found", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @Transactional
    public boolean activateUser(String confirmationString) {
        if (userRepository.activateUser(confirmationString, LocalDate.now()) == 0) {
            throw new CustomRuntimeException("invalid confirmation", HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public static String generateConfirmationString() {
        return RandomStringUtils.random(40, true, true) + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    @SneakyThrows
    @Transactional
    public void uploadAvatar(UUID userId, MultipartFile multipartFile) {
        if (multipartFile.getSize() > MAX_AVATAR_SIZE) {
            throw new CustomRuntimeException("image is too big", HttpStatus.BAD_REQUEST);
        }
        String fileType = multipartFile.getContentType();
        if (fileType == null) {
            throw new CustomRuntimeException("unknown type", HttpStatus.BAD_REQUEST);
        }
        String[] fileParts = fileType.split("/");
        if (!fileParts[0].equals("image")) {
            throw new CustomRuntimeException("unknown type", HttpStatus.BAD_REQUEST);
        }
        String extension = fileParts[fileParts.length - 1];
        storageService.store(AVATAR_PREFIX, userId.toString() + "." + extension, multipartFile.getBytes());
    }


    public AttachmentContentDto loadAvatar(UUID id) {
        try {
            File file = storageService.serveFirstWithoutExtension(AVATAR_PREFIX, id.toString());
            try (FileInputStream inputStream = new FileInputStream(file)) {
                return new AttachmentContentDto(file.getName(), inputStream.readAllBytes());
            }
        } catch (IOException e) {
            return new AttachmentContentDto("", new byte[]{});
        }
    }
}
