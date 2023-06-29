package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String token;
    private String refreshToken;
    private Date issuedAt;
    private long duration;
    private Date refreshValidFrom;
    private long refreshDuration;
}
