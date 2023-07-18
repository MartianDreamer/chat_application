package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenSentDto {
    private String token;
    private String refreshToken;
    private Date issuedAt;
    private long duration;
    private Date refreshValidFrom;
    private long refreshDuration;
}
