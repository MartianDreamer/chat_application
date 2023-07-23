package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.received.AttachmentReceivedDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.repository.AttachmentRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LocalStorageAttachmentService implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Override
    @SneakyThrows
    public List<Attachment> createAttachments(List<AttachmentReceivedDto> dtos) {
        List<Attachment> savedAttachments = attachmentRepository.saveAll(dtos.stream()
                .map(Attachment::from)
                .toList());
        Set<AttachmentReceivedDto> processedDtos = new HashSet<>();
        for (Attachment e : savedAttachments) {
            AttachmentReceivedDto processedDto = dtos.stream()
                    .filter(dto -> e.getFileExtension().equals(dto.getExtension()))
                    .filter(dto -> !processedDtos.contains(dto))
                    .findFirst()
                    .orElseThrow();
            processedDtos.add(processedDto);
            e.setContent(processedDto.getContent().getInputStream());
            persistAttachment(e);
        }
        return savedAttachments;
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
    public List<Attachment> getByConversationId(UUID conversationId) {
        return attachmentRepository.findAllByToId(conversationId);
    }

    private void deleteAttachmentFile(Attachment attachment) {
        String dir = "/attachment/" + attachment.getTo().getId().toString();
        File dirFile = new File(dir);
        String fileName = attachment.getId().toString() + attachment.getFileExtension();
        File file = new File(dirFile, fileName);
        if (!file.delete()) {
            throw new CustomRuntimeException("can not delete attachment " + attachment.getId().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] getAttachmentContent(Attachment attachment) {
        String dir = "/attachment/" + attachment.getTo().getId().toString();
        File dirFile = new File(dir);
        String fileName = attachment.getId().toString() + "." + attachment.getFileExtension();
        File file = new File(dirFile, fileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            throw new CustomRuntimeException("can not get attachment file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<byte[]> getgetAttachmentContents(List<Attachment> attachments) {
        return attachments.stream()
                .map(this::getAttachmentContent)
                .toList();
    }

    private void persistAttachment(Attachment attachment) {
        String dir = "/attachment/" + attachment.getTo().getId().toString();
        File dirFile = new File(dir);
        String fileName = attachment.getId().toString() + "." + attachment.getFileExtension();
        File file = new File(dirFile, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(attachment.getContent().readAllBytes());
        } catch (IOException e) {
            throw new CustomRuntimeException("can not save attachment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
