package home.project.service.security;

import home.project.dto.responseDTO.TokenResponse;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    private Authentication authentication;
    private UserDetails userDetails;
    private final String secretKey = "c2VjcmV0LWtleS1mb3ItdGVzdGluZy1wdXJwb3Nlcy1vbmx5LXNob3VsZC1iZS1sb25nLWVub3VnaA=="; // 테스트용 시크릿 키

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secretKey, userDetailsService);

        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        userDetails = new User("test@example.com", "", authorities);

        authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    @Nested
    @DisplayName("토큰 생성 테스트")
    class GenerateTokenTest {
        @Test
        @DisplayName("인증 정보로 토큰을 정상적으로 생성한다")
        void generateTokenSuccess() {
            TokenResponse tokenResponse = jwtTokenProvider.generateToken(authentication);

            assertThat(tokenResponse).isNotNull();
            assertThat(tokenResponse.getGrantType()).isEqualTo("Bearer");
            assertThat(tokenResponse.getAccessToken()).isNotNull();
            assertThat(tokenResponse.getRefreshToken()).isNotNull();
            assertThat(jwtTokenProvider.validateToken(tokenResponse.getAccessToken())).isTrue();
            assertThat(jwtTokenProvider.validateToken(tokenResponse.getRefreshToken())).isTrue();
        }

        @Test
        @DisplayName("이메일과 ID로 검증 토큰을 생성한다")
        void generateVerificationTokenSuccess() {
            // given
            String email = "test@example.com";
            Long id = 1L;

            String token = jwtTokenProvider.generateVerificationToken(email, id);

            assertThat(token).isNotNull();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
            assertThat(jwtTokenProvider.getEmailFromToken(token)).isEqualTo(email);
            assertThat(jwtTokenProvider.getIdFromVerificationToken(token)).isEqualTo(id.toString());
        }

        @Test
        @DisplayName("비밀번호 재설정 토큰을 생성한다")
        void generateResetTokenSuccess() {
            String email = "test@example.com";

            String resetToken = jwtTokenProvider.generateResetToken(email);

            assertThat(resetToken).isNotNull();
            assertThat(jwtTokenProvider.validateToken(resetToken)).isTrue();
            assertThat(jwtTokenProvider.getEmailFromToken(resetToken)).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("토큰 검증 테스트")
    class ValidateTokenTest {
        @Test
        @DisplayName("유효한 토큰을 검증한다")
        void validateTokenSuccess() {
            TokenResponse tokenResponse = jwtTokenProvider.generateToken(authentication);

            assertThat(jwtTokenProvider.validateToken(tokenResponse.getAccessToken())).isTrue();
            assertThat(jwtTokenProvider.validateTokenDetail(tokenResponse.getAccessToken()))
                    .isEqualTo(JwtTokenProvider.TokenStatus.VALID);
        }

        @Test
        @DisplayName("유효하지 않은 토큰을 검증한다")
        void validateInvalidToken() {
            String invalidToken = "invalid.token.string";

            assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
            assertThat(jwtTokenProvider.validateTokenDetail(invalidToken))
                    .isEqualTo(JwtTokenProvider.TokenStatus.INVALID);
        }
    }

    @Nested
    @DisplayName("토큰 갱신 테스트")
    class RefreshTokenTest {
        @Test
        @DisplayName("리프레시 토큰으로 새로운 액세스 토큰을 발급한다")
        void refreshAccessTokenSuccess() {
            // given
            TokenResponse originalTokens = jwtTokenProvider.generateToken(authentication);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // when
            TokenResponse newTokens = jwtTokenProvider.refreshAccessToken(originalTokens.getRefreshToken());

            assertThat(newTokens).isNotNull();
            assertThat(newTokens.getAccessToken()).isNotNull();
            assertThat(newTokens.getRefreshToken()).isNotNull();
            assertThat(newTokens.getGrantType()).isEqualTo("Bearer");

            assertThat(jwtTokenProvider.validateToken(newTokens.getAccessToken())).isTrue();
            verify(userDetailsService).loadUserByUsername(anyString());
        }

        @Test
        @DisplayName("만료된 리프레시 토큰으로 갱신 시 실패한다")
        void refreshTokenWithExpiredToken() {
            // given
            String expiredToken = "expired.token.string";

            // when & then
            assertThatThrownBy(() -> jwtTokenProvider.refreshAccessToken(expiredToken))
                    .isInstanceOf(JwtException.class)
                    .hasMessageContaining("유효하지 않은 Refresh token입니다.");
        }
    }

    @Nested
    @DisplayName("토큰에서 정보 추출 테스트")
    class ExtractInformationTest {
        @Test
        @DisplayName("토큰에서 인증 정보를 추출한다")
        void getAuthenticationFromTokenSuccess() {
            // given
            TokenResponse tokenResponse = jwtTokenProvider.generateToken(authentication);

            // when
            Authentication extractedAuth = jwtTokenProvider.getAuthentication(tokenResponse.getAccessToken());

            // then
            assertThat(extractedAuth).isNotNull();
            assertThat(extractedAuth.getName()).isEqualTo(authentication.getName());

        }

        @Test
        @DisplayName("권한 정보가 없는 토큰에서 인증 정보 추출 시 실패한다")
        void getAuthenticationFromInvalidTokenFail() {
            String invalidToken = "invalid.token.without.authorities";

            assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(invalidToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("유효하지 않은  token입니다.");
        }
    }
}
