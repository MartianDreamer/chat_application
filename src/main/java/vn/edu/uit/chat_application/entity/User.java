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
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.uit.chat_application.constants.Role;
import vn.edu.uit.chat_application.dto.UserReceivedDto;
import vn.edu.uit.chat_application.service.UserService;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Column(unique = true, length = 58)
    private String confirmationString;

    @Column(unique = true, nullable = false, length = 20)
    @Getter(value = AccessLevel.NONE)
    private String username;

    @Column(nullable = false, length = 30)
    @Getter(value = AccessLevel.NONE)
    private String password;

    @Column(nullable = false)
    @Getter(value = AccessLevel.NONE)
    private boolean accountLocked = false;

    private LocalDate validUntil;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(unique = true, length = 15)
    private String phoneNumber;

    @Lob
    private byte[] avatar;

    @Column(length = 5)
    private String avatarExtension;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String roles;

    @Override
    public Collection<Role> getAuthorities() {
        return Stream.of(roles.split("_"))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
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

    public static User from(UserReceivedDto userReceivedDto) {
        return User.builder()
                .id(userReceivedDto.getId())
                .username(userReceivedDto.getUsername())
                .password(userReceivedDto.getPassword())
                .validUntil(LocalDate.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .email(userReceivedDto.getEmail())
                .phoneNumber(userReceivedDto.getPhoneNumber())
                .avatar(userReceivedDto.getAvatar())
                .avatarExtension(userReceivedDto.getAvatarExtension())
                .confirmationString(UserService.generateConfirmationString())
                .active(false)
                .roles("USER")
                .build();
    }
}
