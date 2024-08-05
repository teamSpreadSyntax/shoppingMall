package home.project.service;

import home.project.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {
    private final Key key;
    private final Long ACCESS_TOKEN_VALIDATION_PERIOD = 60L * 60 * 24 * 1000;
    private final Long REFRESH_TOKEN_VALIDATION_PERIOD = 60L * 60 * 24 * 14 * 1000;
    private final Long VERIFICATION_TOKEN_VALIDATION_PERIOD = 60L * 5 * 1000; // 5분


    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(Authentication authentication) {
        String authorities = getAuthorities(authentication);

        long now = getNow();
        Date accessTokenExpiresIn = getAccessTokenExpiresIn(now);
        String accessToken = getAccessToken(authentication, authorities, accessTokenExpiresIn);

        String refreshToken = getRefreshToken(now);

        return getTokenDTO(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private static long getNow() {
        return (new Date()).getTime();
    }

    private static String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private static TokenDto getTokenDTO(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String getRefreshToken(long now) {
        return Jwts.builder()
                .setExpiration(getRefreshTokenExpires(now))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateVerificationToken(String email, Long id) {
        long now = getNow();
        Date expiresIn = new Date(now + VERIFICATION_TOKEN_VALIDATION_PERIOD);

        return Jwts.builder()
                .setSubject(email)
                .setId(id.toString())
                .setIssuedAt(new Date())
                .setExpiration(expiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromVerificationToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException e) {
            log.error("Invalid verification token", e);
            return null;
        }


    }

    public String getIdFromVerificationToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getId();
        } catch (JwtException e) {
            log.error("Invalid verification token", e);
            return null;
        }
    }
    private Date getRefreshTokenExpires(long now) {
        return new Date(now + REFRESH_TOKEN_VALIDATION_PERIOD);
    }

    private Date getAccessTokenExpiresIn(long now) {
        return new Date(now + ACCESS_TOKEN_VALIDATION_PERIOD);
    }

    private String getAccessToken(Authentication authentication, String authorities, Date accessTokenExpiresIn) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
