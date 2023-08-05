package vn.edu.uit.chat_application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    public void send(String to, String subject,String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setFrom(from);
        message.setTo(to);
        message.setText(content);
        mailSender.send(message);
    }
}
