package vn.edu.uit.chat_application.aspect.processor;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.dto.UserReceivedDto;

import java.util.List;
import java.util.stream.Stream;

@Aspect
@Component
@RequiredArgsConstructor
public class EncryptPasswordProcessor {
    private final BCryptPasswordEncoder passwordEncoder;

    @Before("@annotation(vn.edu.uit.chat_application.aspect.annotation.EncryptPassword)")
    public void encryptPassword(JoinPoint joinPoint) {
        List<UserReceivedDto> userReceivedDtos = Stream.of(joinPoint.getArgs())
                .filter(e -> UserReceivedDto.class.equals(e.getClass()))
                .map(UserReceivedDto.class::cast)
                .toList();
        userReceivedDtos.forEach(e -> e.setPassword(passwordEncoder.encode(e.getPassword())));
    }
}
