package com.sparta.blog.jwt;

import com.sparta.blog.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";             // Header KEY 값
    public static final String AUTHORIZATION_KEY = "auth";                         // 사용자 권한 값의 KEY
    public static final String BEARER_PREFIX = "Bearer ";                          // Token 식별자
    private final long TOKEN_TIME = 60 * 60 * 1000L;                               // 토큰 만료시간 60분
    @Value("${jwt.secret.key}")         // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // 암호화 알고리즘

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);                       // 암호화 디코드
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(String username, UserRoleEnum role) {                 // 토큰 생성
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)                                       // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role)                             // 사용자 권한
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))       // 만료 시간
                        .setIssuedAt(date)                                          // 발급일
                        .signWith(key, signatureAlgorithm)                          // 암호화 알고리즘
                        .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {                    // header 에서 JWT 가져오기
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);                              // `Bearer `공백포함 7자 따기
        }
        return null;
    }

    public boolean validateToken(String token) {                                   // 토큰 검증
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {                            // 토큰에서 사용자 정보 가져오기
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}