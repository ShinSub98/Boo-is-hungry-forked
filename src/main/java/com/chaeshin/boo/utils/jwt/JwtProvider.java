package com.chaeshin.boo.utils.jwt;

import com.chaeshin.boo.exception.ExpiredTokenException;
import com.chaeshin.boo.exception.TokenNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtProvider {

    static private String issuer;
    static private Long accessExpiration;
    static private Long refreshExpiration;
    static private SecretKey secretKey;
    static private TokenBlackList blackList;

    @Autowired
    public JwtProvider(@Value("${spring.application.name}") String issuer,
                       @Value("${service.jwt.access-expiration}") Long accessExpiration,
                       @Value("${service.jwt.refresh-expiration}") Long refreshExpiration,
                       @Value("${service.jwt.secret-key}") String secretKey,
                       TokenBlackList blackList) {
        this.issuer = issuer;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.blackList = blackList;
    }

    public enum TokenType {
        ACCESS, REFRESH
    }


    /*액세스 토큰 발급*/
    public String createAccessToken(Long memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(memberId.toString())
                .claim("type", TokenType.ACCESS)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }


    /*리프레시 토큰 발급*/
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(memberId.toString())
                .claim("type", TokenType.REFRESH)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }


    /*리프레시 토큰 검증 후 새로운 액세스 토큰 발급*/
    public Map<String, String> refreshAccessToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(refreshToken);
            if (!claims.getPayload().get("type").toString().equals(TokenType.REFRESH.toString())) {
                throw new RuntimeException("bool" + (claims.getPayload().get("type").equals(TokenType.REFRESH)));
            }
            if (claims.getPayload().getIssuedAt().after(new Date())) {
                throw new RuntimeException("발급시간: " + claims.getPayload().getIssuedAt() + ", 현재시간: " + new Date().toString());
            }
            Date expireAt = claims.getPayload().getExpiration();
            if (expireAt.before(new Date())) {
                throw new RuntimeException("만료된 리프레시 토큰");
            }
            if (blackList.containsKey(refreshToken)) {
                throw new RuntimeException("블랙리스트 처리된 리프레시 토큰");
            }
            Long memberId = Long.parseLong(claims.getPayload().getSubject());
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("access", createAccessToken(memberId));
            tokenMap.put("refresh", createRefreshToken(memberId));
            blackList.put(refreshToken, expireAt);
            return tokenMap;
        } catch (Exception es) {
            throw es;
        }
    }


    /*토큰 유효성, 만료일자 확인*/
    public boolean validateToken(HttpServletRequest request) {
        String token = this.resolveToken(request);
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return !claims.getPayload().getExpiration().before(new Date())
                    && !claims.getPayload().getIssuedAt().after(new Date())
                    && claims.getPayload().get("type").equals(TokenType.ACCESS);
        } catch (Exception e) {
            throw new ExpiredTokenException();
        }
    }


    /*요청 헤더에서 토큰 추출*/
    public String resolveToken(HttpServletRequest request) {
        try{
            String bearerToken = request.getHeader("Authorization");
            String token = bearerToken.replace("Bearer ", "");
            return token;
        } catch (RuntimeException e) {
            throw new TokenNotFoundException();
        }
    }


    /*요청에서 유저 ID 추출*/
    public Long getMemberId(HttpServletRequest request) {
        String token = resolveToken(request);
        String memberId = Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
        return Long.parseLong(memberId);
    }
}
