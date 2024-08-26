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
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
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

    public void validateTokenResult(String accessToken, String refreshToken) {
        // 리프레시 토큰 유효성 검사
        if (!validateToken(refreshToken)) {
            throw new JwtException("유효하지 않은 Refresh token입니다.");
        }

        // 만료된 액세스 토큰에서 사용자 정보 추출
        Claims claims = parseClaims(accessToken);
        String username = claims.getSubject();
        String authorities = claims.get("auth", String.class);

        if (username == null || authorities == null) {
            throw new JwtException("유효하지 않은 Access token입니다.");
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token", e);
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

    public TokenDto refreshAccessToken(String expiredAccessToken, String refreshToken) {
        // 리프레시 토큰 유효성 검사
        if (!validateToken(refreshToken)) {
            throw new JwtException("유효하지 않은 Refresh token입니다.");
        }

        // 만료된 액세스 토큰에서 사용자 정보 추출
        Claims claims = parseClaims(expiredAccessToken);
        String username = claims.getSubject();
        String authorities = claims.get("auth", String.class);

        if (username == null || authorities == null) {
            throw new JwtException("유효하지 않은 Access token입니다.");
        }

        // 사용자 정보와 권한 가져오기 (이 부분은 실제 구현에 맞게 수정 필요)
        // 예를 들어, UserDetailsService를 사용하여 사용자 정보를 가져올 수 있습니다.
        // UserDetails 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 새로운 Authentication 객체 생성
        Collection<? extends GrantedAuthority> grantedAuthorities =
                Arrays.stream(authorities.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);

        // 새로운 액세스 토큰 생성
        long now = getNow();
        Date accessTokenExpiresIn = getAccessTokenExpiresIn(now);
        String newAccessToken = getAccessToken(authentication, authorities, accessTokenExpiresIn);

        // 새로운 리프레시 토큰 생성 (선택적)
        String newRefreshToken = getRefreshToken(now);

        // 새로운 TokenDto 반환
        return getTokenDTO(newAccessToken, newRefreshToken);
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
            return null;
        }
    }
    public String getEmailFromAccessToken(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException e) {
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
