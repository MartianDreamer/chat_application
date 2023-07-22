package vn.edu.uit.chat_application.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void announce(String string) {
        simpMessagingTemplate.convertAndSendToUser("asd", "adsasd", string);
    }
}
