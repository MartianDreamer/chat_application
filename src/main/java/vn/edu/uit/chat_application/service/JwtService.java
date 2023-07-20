package vn.edu.uit.chat_application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.dto.sent.TokenSentDto;
import vn.edu.uit.chat_application.entity.User;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.duration}")
    private Long duration;
    @Value("${jwt.refresh.duration}")
    private Long refreshDuration;

    private Key getKey() {
        return new SecretKeySpec(Base64.getEncoder().encode(secret.getBytes()), SignatureAlgorithm.HS256.getJcaName());
    }

    public TokenSentDto issueTokenPair(User user) {
        Date now = new Date();
        Date refreshValidFrom = Date.from(now.toInstant().plus(duration, ChronoUnit.SECONDS));
        Date refreshExpired = Date.from(refreshValidFrom.toInstant().plus(refreshDuration, ChronoUnit.SECONDS));
        String accessToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(refreshValidFrom)
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("authority", user.getAuthorities())
                .signWith(getKey())
                .compact();
        String refreshToken = Jwts.builder()
                .setIssuedAt(refreshValidFrom)
                .setExpiration(refreshExpired)
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .signWith(getKey())
                .compact();
        return new TokenSentDto(accessToken, refreshToken, now, duration, refreshValidFrom, refreshDuration);
    }


    public Optional<UUID> validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.of(UUID.fromString(claims.getSubject()));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
