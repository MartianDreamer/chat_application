package vn.edu.uit.chat_application.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN;
    @Override
    public String getAuthority() {
        return this.toString();
    }
}
