package vn.edu.uit.chat_application.controller.rest;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.chat_application.dto.received.AnnouncementReceivedDto;
import vn.edu.uit.chat_application.dto.sent.AnnouncementSentDto;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/rest/admin")
@RequiredArgsConstructor
public class AdminController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/announcement")
    @RolesAllowed(value = {"ADMIN"})
    public void announce(@RequestBody AnnouncementReceivedDto announcementReceivedDto) {
        if (announcementReceivedDto.getTos() == null || announcementReceivedDto.getTos().isEmpty()) {
            AnnouncementSentDto sentDto = new AnnouncementSentDto(announcementReceivedDto.getContent(), LocalDateTime.now());
            simpMessagingTemplate.convertAndSend("/topic/announcement", sentDto);
            return;
        }
        announcementReceivedDto.getTos().forEach(e -> {
            AnnouncementSentDto sentDto = new AnnouncementSentDto(announcementReceivedDto.getContent(), LocalDateTime.now());
            simpMessagingTemplate.convertAndSendToUser(e, "/queue/announcement", sentDto);
        });
    }
}
