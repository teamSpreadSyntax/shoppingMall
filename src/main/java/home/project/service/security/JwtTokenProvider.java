package home.project.service.security;

import home.project.dto.responseDTO.TokenResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Service
public class JwtTokenProvider {
    private final Key key;
    private final Long ACCESS_TOKEN_VALIDATION_PERIOD = 30L * 60 * 1000;
    private final Long REFRESH_TOKEN_VALIDATION_PERIOD = 14L * 24 * 60 * 60 * 1000;
    private final Long VERIFICATION_TOKEN_VALIDATION_PERIOD = 24L * 60 * 60 * 1000;
    private final Long RESET_TOKEN_VALIDATION_PERIOD = 10L * 60 * 1000;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
    }

    public TokenResponse generateToken(Authentication authentication) {
        String authorities = getAuthorities(authentication);

        long now = getNow();
        String accessToken = buildToken(authentication.getName(), authorities, now, ACCESS_TOKEN_VALIDATION_PERIOD);
        String refreshToken = buildToken(authentication.getName(), authorities, now, REFRESH_TOKEN_VALIDATION_PERIOD);
        return getTokenDTO(accessToken, refreshToken);
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

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void validateTokenResult(String accessToken, String refreshToken) {
        throwExceptionForInvalidToken(validateTokenDetail(accessToken), "Access");
        throwExceptionForInvalidToken(validateTokenDetail(refreshToken), "Refresh");
    }

    public TokenStatus validateTokenDetail(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException e) {
            return TokenStatus.INVALID;
        }
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        throwExceptionForInvalidToken(validateTokenDetail(refreshToken), "Refresh");

        Claims claims = parseClaims(refreshToken);
        String username = claims.getSubject();
        String authorities = claims.get("auth", String.class);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = createAuthentication(userDetails, authorities);

        return generateToken(authentication);
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

    public String getIdFromVerificationToken(String token) {
        throwExceptionForInvalidToken(validateTokenDetail(token), "Verification");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getId();
    }

    public String getEmailFromToken(String token) {
        throwExceptionForInvalidToken(validateTokenDetail(token), "");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    private Claims parseClaims(String accessToken) {
        throwExceptionForInvalidToken(validateTokenDetail(accessToken), "");
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

    }

    static long getNow() {
        return (new Date()).getTime();
    }

    private String buildToken(String subject, String authorities, long now, long validityPeriod) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("auth", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validityPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private static TokenResponse getTokenDTO(String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void throwExceptionForInvalidToken(TokenStatus status, String tokenType) {
        switch (status) {
            case VALID:
                return;
            case EXPIRED:
                throw new JwtException("만료된 " + tokenType + " token입니다. 다시 로그인 해주세요.");
            case INVALID:
            default:
                throw new JwtException("유효하지 않은 " + tokenType + " token입니다.");
        }
    }

    private Authentication createAuthentication(UserDetails userDetails, String authorities) {
        Collection<? extends GrantedAuthority> grantedAuthorities =
                Arrays.stream(authorities.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);
    }

    public enum TokenStatus {
        VALID,
        INVALID,
        EXPIRED
    }

    public String generateResetToken(String email) {
        long now = getNow();
        Date expirationDate = new Date(now + RESET_TOKEN_VALIDATION_PERIOD);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
