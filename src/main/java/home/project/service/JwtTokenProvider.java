package home.project.service;

import home.project.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
        String refreshToken = getRefreshToken(authentication, authorities, now);
        return getTokenDTO(accessToken, refreshToken);
    }

    private String getAccessToken(Authentication authentication, String authorities, Date accessTokenExpiresIn) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String getRefreshToken(Authentication authentication, String authorities,long now) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
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

    public TokenDto refreshAccessToken(String refreshToken) {

        throwExceptionForInvalidToken(validateTokenDetail(refreshToken), "Refresh");

                Claims claims = parseClaims(refreshToken);
                String username = claims.getSubject();
                String authorities = claims.get("auth", String.class);


                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                Collection<? extends GrantedAuthority> grantedAuthorities =
                        Arrays.stream(authorities.split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);

                long now = getNow();
                Date accessTokenExpiresIn = getAccessTokenExpiresIn(now);
                String newAccessToken = getAccessToken(authentication, authorities, accessTokenExpiresIn);

                String newRefreshToken = getRefreshToken(authentication, authorities,now);

                return getTokenDTO(newAccessToken, newRefreshToken);

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

    private Date getRefreshTokenExpires(long now) {
        return new Date(now + REFRESH_TOKEN_VALIDATION_PERIOD);
    }

    private Date getAccessTokenExpiresIn(long now) {
        return new Date(now + ACCESS_TOKEN_VALIDATION_PERIOD);
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

    public enum TokenStatus {
        VALID,
        INVALID,
        EXPIRED
    }

}
