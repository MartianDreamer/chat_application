package vn.edu.uit.chat_application.constants;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER, ROLE_ADMIN;
    @Override
    public String getAuthority() {
        return this.toString();
    }
}
