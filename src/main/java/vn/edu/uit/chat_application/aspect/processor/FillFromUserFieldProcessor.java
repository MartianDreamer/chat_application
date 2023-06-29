package vn.edu.uit.chat_application.aspect.processor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.dto.FromLoggedInUserDto;
import vn.edu.uit.chat_application.util.PrincipalUtils;

import java.util.stream.Stream;

@Aspect
@Component
public class FillFromUserFieldProcessor {
    @Before("@annotation(vn.edu.uit.chat_application.aspect.annotation.FillFromUserField)")
    public void fillFromUserField(JoinPoint joinPoint) {
        Stream.of(joinPoint.getArgs())
                .filter(e -> e instanceof FromLoggedInUserDto)
                .map(FromLoggedInUserDto.class::cast)
                .forEach(e -> e.setFrom(PrincipalUtils.getLoggedInUser()));
    }
}
