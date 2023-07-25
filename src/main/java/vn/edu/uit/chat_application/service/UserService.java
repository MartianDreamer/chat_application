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
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.UserRepository;
import vn.edu.uit.chat_application.util.CommonUtils;

import java.io.IOException;
import java.io.InputStream;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findDistinctByUsername(username);
    }

    @EncryptPassword
    public String createUser(UserReceivedDto dto) {
        return userRepository.save(User.from(dto)).getConfirmationString();
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
        User user = userRepository.findDistinctByUsername(username);
        if (user == null) {
            throw CustomRuntimeException.notFound();
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
        storageService.store("/avatar", userId.toString() + "." + extension, multipartFile.getBytes());
        userRepository.uploadAvatar(userId, extension);
    }


    public byte[] loadAvatar(UUID id) {
        return findById(id)
                .map(e -> {
                    try (InputStream inputStream = storageService.serve("/avatar", id.toString() + "." + e.getAvatarExtension())) {
                        return inputStream.readAllBytes();
                    } catch (IOException ex) {
                        throw new CustomRuntimeException("can not load avatar", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(CustomRuntimeException::notFound);
    }
}
