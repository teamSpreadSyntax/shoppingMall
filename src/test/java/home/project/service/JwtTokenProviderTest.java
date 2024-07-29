package home.project.service;

import home.project.domain.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {


    private JwtTokenProvider jwtTokenProvider;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        String secretKey = "thisisaverylongsecretkeythisisaverylongsecretkey";

        jwtTokenProvider = new JwtTokenProvider(secretKey);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails userDetails = new User("user", "password", authorities);

        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    @Nested
    class generateTokenTests {
        @Test
        void generateToken_validAuthentication_returnsValidToken() {

            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

            assertNotNull(tokenDto);
            assertNotNull(tokenDto.getAccessToken());
            assertNotNull(tokenDto.getRefreshToken());
            assertEquals("Bearer", tokenDto.getGrantType());
        }
    }
    @Nested
    class getAuthenticationTests {

        @Test
        void getAuthentication_validToken_returnsAuthenticationWithRoleUser() {
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        Authentication auth = jwtTokenProvider.getAuthentication(tokenDto.getAccessToken());

        assertNotNull(auth);
        assertEquals("ROLE_USER", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()));
        assertEquals(auth.getName(), authentication.getName());
        }
        @Test
        void getAuthentication_tokenWithoutAuthority_throwsRuntimeException() {
        String tokenWithoutAuth = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("thisisaverylongsecretkeythisisaverylongsecretkey")), SignatureAlgorithm.HS256)
                .compact();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> jwtTokenProvider.getAuthentication(tokenWithoutAuth));
        assertEquals("권한 정보가 없는 토큰입니다.", exception.getMessage());
        }
    }
    @Nested
    class validateTokenTests {
        @Test
        void validateToken_validToken_returnsTrue() {

            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

            boolean isValid = jwtTokenProvider.validateToken(tokenDto.getAccessToken());

            assertTrue(isValid);
        }

        @Test
        void validateToken_invalidToken_returnsFalse() {
            String wrongKeyToken = Jwts.builder()
                    .setSubject("user")
                    .setExpiration(new Date(System.currentTimeMillis() + 10000))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("wrongKeywrongKeywrongKeywrongKeywrongKeywrongKey")), SignatureAlgorithm.HS256)
                    .compact();

            boolean isValid = jwtTokenProvider.validateToken(wrongKeyToken);

            assertFalse(isValid);
        }

        @Test
        void validateToken_expiredToken_returnsFalse() {
            String expiredToken = Jwts.builder()
                    .setSubject("user")
                    .setExpiration(new Date(System.currentTimeMillis() - 1000))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("thisisaverylongsecretkeythisisaverylongsecretkey")), SignatureAlgorithm.HS256)
                    .compact();

            boolean isValid = jwtTokenProvider.validateToken(expiredToken);

            assertFalse(isValid);
        }
    }
}
