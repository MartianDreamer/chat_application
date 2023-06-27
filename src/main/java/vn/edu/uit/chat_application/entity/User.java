package vn.edu.uit.chat_application.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.uit.chat_application.constants.FileExtension;
import vn.edu.uit.chat_application.constants.Role;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "T_USER")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(length = 40)
    private String confirmationString = RandomStringUtils.random(40, true, true);

    @Column(unique = true, nullable = false, length = 20)
    @Getter(value = AccessLevel.NONE)
    private String username;

    @Column(nullable = false, length = 30)
    @Getter(value = AccessLevel.NONE)
    private String password;

    @Column(nullable = false)
    @Getter(value = AccessLevel.NONE)
    private boolean accountLocked;

    private LocalDate validUntil;

    @Column(nullable = false)
    private boolean online;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(unique = true, length = 15)
    private String phoneNumber;

    @Lob
    private byte[] avatar;

    private FileExtension.ImageFileExtension avatarExtension;

    @Column(nullable = false)
    private boolean isConfirmed;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "T_USER_ROLES")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<Role> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
