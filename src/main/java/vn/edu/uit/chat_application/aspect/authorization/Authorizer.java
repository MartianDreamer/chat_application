package vn.edu.uit.chat_application.aspect.authorization;

public interface Authorizer {
    boolean authorize(MethodType methodType, Object[] args, Object returnValue);
}
