package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.AttachmentReceivedDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.AttachmentRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LocalStorageAttachmentService implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Override
    public List<Attachment> createAttachments(List<AttachmentReceivedDto> dtos) {
        return dtos.stream()
                .map(Attachment::from)
                .peek(attachmentRepository::save)
                .peek(this::persistAttachment)
                .toList();
    }

    @Override
    public void deleteAttachmens(List<UUID> ids) {
        List<Attachment> attachments = attachmentRepository.findAllById(ids);
        attachmentRepository.deleteAllById(attachments.stream()
                .peek(this::deleteAttachmentFile)
                .map(Attachment::getId)
                .toList());
    }

    @Override
    public List<Attachment> getAttachments(List<UUID> ids) {
        return attachmentRepository.findAllById(ids).stream()
                .map(this::getAttachmentContent)
                .toList();
    }

    @Override
    public List<Attachment> getByConversationId(UUID conversationId) {
        return attachmentRepository.findAllByToId(conversationId);
    }


    public void deleteAttachmentFile(Attachment attachment) {
        String dir = "/attachment/" + attachment.getTo().getId().toString();
        File dirFile = new File(dir);
        String fileName = attachment.getId().toString() + attachment.getFileExtension();
        File file = new File(dirFile, fileName);
        if (!file.delete()) {
            throw new CustomRuntimeException("can not delete attachment " + attachment.getId().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Attachment getAttachmentContent(Attachment attachment) {
        String dir = "/attachment/" + attachment.getTo().getId().toString();
        File dirFile = new File(dir);
        String fileName = attachment.getId().toString() + attachment.getFileExtension();
        File file = new File(dirFile, fileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            attachment.setContent(fileInputStream.readAllBytes());
            return attachment;
        } catch (IOException e) {
            throw new CustomRuntimeException("can not get attachment file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void persistAttachment(Attachment attachment) {
        String dir = "/attachment/" + attachment.getTo().getId().toString();
        File dirFile = new File(dir);
        String fileName = attachment.getId().toString() + attachment.getFileExtension();
        File file = new File(dirFile, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(attachment.getContent());
        } catch (IOException e) {
            throw new CustomRuntimeException("can not save attachment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
