package vn.edu.uit.chat_application.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.chat_application.dto.sent.AttachmentContentDto;
import vn.edu.uit.chat_application.entity.Attachment;
import vn.edu.uit.chat_application.service.AttachmentService;
import vn.edu.uit.chat_application.service.NotificationService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final NotificationService notificationService;
    @PostMapping("/{conversationId}")
    @Transactional
    public void sendAttachment(@PathVariable("conversationId") UUID conversationId, @RequestParam("file") MultipartFile[] files) {
        Attachment attachment = attachmentService.create(conversationId, Arrays.asList(files));
        notificationService.sendAttachmentNotification(attachment);
    }

    @GetMapping("/{attachmentId}")
    public @ResponseBody List<AttachmentContentDto> getAttachment(@PathVariable("attachmentId") UUID attachmentId) {
        return attachmentService.getContents(attachmentId);
    }

    @DeleteMapping("/{attachmentId}")
    public void deleteAttachment(@PathVariable("attachmentId") UUID attachmentId) {
        attachmentService.delete(attachmentId);
    }
}
