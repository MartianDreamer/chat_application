package vn.edu.uit.chat_application.aspect.authorization;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class AuthorizationProcessor {

    protected final Class<?> authorizedClass;
    protected final Map<String, Method> methodMap = Stream.of(this.getClass().getDeclaredMethods())
            .collect(Collectors.toMap(Method::getName, e -> e));

    @SneakyThrows
    public void authorize(JoinPoint joinPoint) {
        Method authorizeMethod = methodMap.get(joinPoint.getSignature().getName());
        if (authorizeMethod != null) {
            authorizeMethod.invoke(this, joinPoint);
        }
    }

}
