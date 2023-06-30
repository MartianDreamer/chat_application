package vn.edu.uit.chat_application.aspect.authorization;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vn.edu.uit.chat_application.aspect.annotation.AllowedMethod;
import vn.edu.uit.chat_application.dto.received.UserReceivedDto;
import vn.edu.uit.chat_application.entity.User;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;
import vn.edu.uit.chat_application.service.UserService;

import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationProcessor {

    private static final String ENTITY_PACKAGE = User.class.getPackageName();
    private static final String RECEIVED_DTO_PACKAGE = UserReceivedDto.class.getPackageName();
    private final UserServiceAuthorizer userServiceAuthorizer;
    private Map<Class<?>, Authorizer> authorizerMap;

    @PostConstruct
    private void initAuthorizerMap() {
        authorizerMap = Map.ofEntries(
                Map.entry(UserService.class, userServiceAuthorizer)
        );
    }

    @AfterReturning(value = "within(vn.edu.uit.chat_application.service..*)", returning = "returnValue")
    public void authorize(JoinPoint joinPoint, Object returnValue) {
        if (isAllowedMethod(joinPoint)) {
            return;
        }
        Class<?> processedClass = joinPoint.getThis().getClass();
        if (authorizerMap.containsKey(processedClass)) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        Class<?> returnType = returnValue.getClass();
        Class<?> returnParameterType = null;
        if (returnValue instanceof Collection<?>) {
            TypeVariable<? extends Class<?>>[] parameterTypes = returnType.getTypeParameters();
            returnParameterType = parameterTypes.length == 1
                    ? parameterTypes[0].getGenericDeclaration()
                    : parameterTypes[1].getGenericDeclaration();
        }
        MethodType methodType = getMethodType(args, returnType, returnParameterType);
        if (!authorizerMap.get(processedClass).authorize(methodType, args, returnValue)) {
            throw new CustomRuntimeException("forbidden", HttpStatus.FORBIDDEN);
        }
    }
    
    private MethodType getMethodType(Object[] args, Class<?> returnType, Class<?> returnParameterType) {
        if (isMultipleDeletionMethod(args, returnType) || isMultipleCreationOrUpdateMethod(args, returnParameterType)) {
            return MethodType.MULTIPLE_WRITE;
        } else if (isMultipleReadMethod(args, returnParameterType)) {
            return MethodType.MULTIPLE_READ;
        } else if (isSingleDeletionMethod(args, returnType) || isSingleCreationOrUpdateMethod(args, returnType)) {
            return MethodType.SINGLE_WRITE;
        } else if (isSingleReadMethod(args, returnType)) {
            return MethodType.SINGLE_READ;
        }
        return MethodType.UNIDENTIFIED;
    }

    private boolean isSingleDeletionMethod(Object[] args, Class<?> returnType) {
        return args.length == 1
                && Void.TYPE.equals(returnType)
                && args[0] instanceof UUID;
    }

    private boolean isMultipleDeletionMethod(Object[] args, Class<?> returnType) {
        return args.length == 1
                && Void.TYPE.equals(returnType)
                && args[0] instanceof Collection<?>
                && args[0].getClass().getTypeParameters()[0].getGenericDeclaration().equals(UUID.class);
    }

    private boolean isSingleCreationOrUpdateMethod(Object[] args, Class<?> returnType) {
        return returnType.getPackageName().equals(ENTITY_PACKAGE)
                && args.length == 1
                && args.getClass().getPackageName().equals(RECEIVED_DTO_PACKAGE);
    }

    private boolean isMultipleCreationOrUpdateMethod(Object[] args, Class<?> returnParameterType) {
        return returnParameterType != null
                && returnParameterType.getPackageName().equals(ENTITY_PACKAGE)
                && args.length == 1
                && args[0] instanceof Collection<?>
                && args[0].getClass().getTypeParameters()[0].getGenericDeclaration().getPackageName().equals(RECEIVED_DTO_PACKAGE);
    }

    private boolean isSingleReadMethod(Object[] args, Class<?> returnType) {
        return !isSingleCreationOrUpdateMethod(args, returnType) && returnType.getPackageName().equals(ENTITY_PACKAGE);
    }

    private boolean isMultipleReadMethod(Object[] args, Class<?> returnParameterType) {
        return !isMultipleCreationOrUpdateMethod(args, returnParameterType) && returnParameterType.getPackageName().equals(ENTITY_PACKAGE);
    }

    private boolean isAllowedMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = ((MethodSignature) joinPoint.getSignature());
        return (methodSignature.getModifiers() & Modifier.PRIVATE) == Modifier.PRIVATE
                || methodSignature
                .getMethod()
                .getDeclaredAnnotation(AllowedMethod.class) != null;
    }
}
