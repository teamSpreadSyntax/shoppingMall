package home.project.service;

import home.project.domain.Member;
import home.project.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Authentication authentication;

    private TokenDto tokenDto;

    private Member member;

    private String invalidToken;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        String secretKey = "thisisaverylongsecretkeythisisaverylongsecretkey";

        jwtTokenProvider = new JwtTokenProvider(secretKey, userDetailsService);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails userDetails = new User("user@user.com", "password", authorities);

        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        tokenDto = jwtTokenProvider.generateToken(authentication);

        member = new Member();
        member.setId(1L);
        member.setEmail("test@test.com");

        invalidToken = "invalidToken";
    }

    @Nested
    class generateTokenTests {
        @Test
        void generateToken_ValidAuthentication_ReturnsValidToken() {

            assertNotNull(tokenDto);
            assertNotNull(tokenDto.getAccessToken());
            assertNotNull(tokenDto.getRefreshToken());
            assertEquals("Bearer", tokenDto.getGrantType());
        }
    }

    @Nested
    class generateVerificationTokenTest {
        @Test
        void generateVerificationToken_ValidInput_ReturnsToken() {
            String token = jwtTokenProvider.generateVerificationToken(member.getEmail(), member.getId());

            assertNotNull(token);
        }
    }

    @Nested
    class validateTokenTests {
        @Test
        void validateToken_ValidToken_ReturnsTrue() {

            boolean isValid = jwtTokenProvider.validateToken(tokenDto.getAccessToken());

            assertTrue(isValid);
        }

        @Test
        void validateToken_InvalidToken_ReturnsFalse() {
            String wrongKeyToken = Jwts.builder()
                    .setSubject("user")
                    .setExpiration(new Date(System.currentTimeMillis() + 10000))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("wrongKeywrongKeywrongKeywrongKeywrongKeywrongKey")), SignatureAlgorithm.HS256)
                    .compact();

            boolean isValid = jwtTokenProvider.validateToken(wrongKeyToken);

            assertFalse(isValid);
        }

        @Test
        void validateToken_ExpiredToken_ReturnsFalse() {
            String expiredToken = Jwts.builder()
                    .setSubject("user")
                    .setExpiration(new Date(System.currentTimeMillis() - 1000))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("thisisaverylongsecretkeythisisaverylongsecretkey")), SignatureAlgorithm.HS256)
                    .compact();

            boolean isValid = jwtTokenProvider.validateToken(expiredToken);

            assertFalse(isValid);
        }
    }
    @Nested
    class ValidateTokenResultTests {
        @Test
        void validateTokenResult_ValidTokens_DoesNotThrowJwtException() {
            assertDoesNotThrow(() -> jwtTokenProvider.validateTokenResult(
                    tokenDto.getAccessToken(), tokenDto.getRefreshToken()));
        }

        @Test
        void validateTokenResult_InvalidAccessToken_ThrowsJwtException() {
            String invalidAccessToken = "invalidAccessToken";

            JwtException exception = assertThrows(JwtException.class, () ->
                    jwtTokenProvider.validateTokenResult(invalidAccessToken, tokenDto.getRefreshToken()));
            assertEquals("유효하지 않은 Access token입니다.", exception.getMessage());
        }

        @Test
        void validateTokenResult_ExpiredAccessToken_ThrowsJwtException() {
            String expiredAccessToken = Jwts.builder()
                    .setSubject("user")
                    .setExpiration(new Date(System.currentTimeMillis() - 1000))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("thisisaverylongsecretkeythisisaverylongsecretkey")), SignatureAlgorithm.HS256)
                    .compact();

            JwtException exception = assertThrows(JwtException.class, () ->
                    jwtTokenProvider.validateTokenResult(expiredAccessToken, tokenDto.getRefreshToken()));
            assertEquals("만료된 Access token입니다. 다시 로그인 해주세요.", exception.getMessage());
        }

        @Test
        void validateTokenResult_InvalidRefreshToken_ThrowsJwtException() {
            String invalidRefreshToken = "invalidRefreshToken";

            JwtException exception = assertThrows(JwtException.class, () ->
                    jwtTokenProvider.validateTokenResult(tokenDto.getAccessToken(), invalidRefreshToken));
            assertEquals("유효하지 않은 Refresh token입니다.", exception.getMessage());
        }
    }

    @Nested
    class RefreshAccessTokenTests {
        @Test
        void refreshAccessToken_ValidTokens_ReturnsNewTokens() {
            String refreshToken = jwtTokenProvider.generateToken(authentication).getRefreshToken();

            UserDetails userDetails = new User("user@user.com", "password", authentication.getAuthorities());
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            TokenDto newTokenDto = jwtTokenProvider.refreshAccessToken(refreshToken);

            assertNotNull(newTokenDto);
            assertNotNull(newTokenDto.getAccessToken());
            assertNotNull(newTokenDto.getRefreshToken());
        }

        @Test
        void refreshAccessToken_InvalidRefreshToken_ThrowsJwtException() {
            String invalidRefreshToken = "invalidRefreshToken";

            assertThrows(JwtException.class, () ->
                    jwtTokenProvider.refreshAccessToken(invalidRefreshToken)
            );
        }
    }

    @Nested
    class getAuthenticationTests {

        @Test
        void getAuthentication_ValidToken_ReturnsAuthenticationWithRoleUser() {

            Authentication auth = jwtTokenProvider.getAuthentication(tokenDto.getAccessToken());

            assertNotNull(auth);
            assertEquals("ROLE_USER", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()));
            assertEquals(auth.getName(), authentication.getName());
        }
        @Test
        void getAuthentication_TokenWithoutAuthority_ThrowsRuntimeException() {
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
    class GetIdFromVerificationTokenTests {

        @Test
        void getIdFromVerificationToken_ValidToken_ReturnsId() {
            String token = jwtTokenProvider.generateVerificationToken(member.getEmail(), member.getId());

            String extractedId = jwtTokenProvider.getIdFromVerificationToken(token);

            assertEquals(member.getId().toString(), extractedId);
        }

        @Test
        void getIdFromVerificationToken_InvalidToken_ReturnsNull() {
            String extractedId = jwtTokenProvider.getIdFromVerificationToken(invalidToken);

            assertNull(extractedId);
        }
    }

    @Nested
    class GetEmailFromTokenTests {

        @Test
        void getEmailFromToken_ValidToken_ReturnsEmail() {
            String accessToken = jwtTokenProvider.generateToken(authentication).getAccessToken();

            String email = jwtTokenProvider.getEmailFromToken(accessToken);

            assertEquals("user@user.com",email);
        }


        @Test
        void getEmailFromToken_InvalidToken_ThrowsJwtException() {
            String invalidToken = "invalidToken";

            JwtException exception = assertThrows(JwtException.class, () -> jwtTokenProvider.getEmailFromToken(invalidToken));
            assertEquals("유효하지 않은 Verification token입니다.", exception.getMessage());
        }

        @Test
        void getEmailFromToken_ExpiredToken_ThrowsJwtException() {
            String expiredToken = Jwts.builder()
                    .setSubject("user@user.com")
                    .setExpiration(new Date(System.currentTimeMillis() - 1000))
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("thisisaverylongsecretkeythisisaverylongsecretkey")), SignatureAlgorithm.HS256)
                    .compact();

            JwtException exception = assertThrows(JwtException.class, () -> jwtTokenProvider.getEmailFromToken(expiredToken));
            assertEquals("만료된  Verification token입니다. 본인확인을 다시 진행해주세요.", exception.getMessage());
        }
    }

}
