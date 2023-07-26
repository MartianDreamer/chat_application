package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.chat_application.dto.sent.AttachmentContentDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.entity.Conversation;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.AttachmentRepository;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static vn.edu.uit.chat_application.constants.Constants.MAX_ATTACHMENT_SIZE;

@RequiredArgsConstructor
@Service
public class LocalStorageAttachmentService implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final StorageService localStorage;
    private static final String ATTACHMENT_PREFIX = "/attachment";

    @Override
    @Transactional
    public Attachment create(UUID toId, List<MultipartFile> multipartFiles) {
        List<String> fileNames = multipartFiles.stream()
                .map(MultipartFile::getName)
                .toList();
        User from = PrincipalUtils.getLoggedInUser();
        Attachment attachment = attachmentRepository.save(new Attachment(new Conversation(toId), from, fileNames, LocalDateTime.now()));
        multipartFiles.forEach(e -> {
            try {
                if (e.getSize() > MAX_ATTACHMENT_SIZE) {
                    throw new CustomRuntimeException("attachment is bigger than 30MB", HttpStatus.BAD_REQUEST);
                }
                localStorage.store(ATTACHMENT_PREFIX + "/" + attachment.getId(), e.getName(), e.getBytes());
            } catch (IOException ex) {
                throw new CustomRuntimeException("can not save attachment", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
        return attachment;
    }

    @Override
    public void delete(UUID attachmentId) {
        attachmentRepository.deleteById(attachmentId);
        localStorage.delete(ATTACHMENT_PREFIX + "/" + attachmentId);
    }

    @Override
    public List<AttachmentContentDto> getContents(UUID attachmentId) {
        return localStorage.serve(ATTACHMENT_PREFIX + "/" + attachmentId).stream()
                .map(e -> {
                    try (FileInputStream inputStream = new FileInputStream(e)) {
                        return new AttachmentContentDto(e.getName(), inputStream.readAllBytes());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .toList();
    }
}
