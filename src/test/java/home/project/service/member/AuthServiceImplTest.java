package home.project.service.member;

import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.LoginRequestDTO;
import home.project.dto.responseDTO.TokenResponse;
import home.project.repository.member.MemberRepository;
import home.project.service.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {

    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Member testMember;
    private LoginRequestDTO loginRequest;
    private TokenResponse tokenResponse;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        String testEmail = "test@test.com";
        String testPassword = "Password123!";

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail(testEmail);
        testMember.setPassword("encoded_password");
        testMember.setRole(RoleType.user);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testEmail);
        when(userDetails.getPassword()).thenReturn(testMember.getPassword());

        tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("access_token");
        tokenResponse.setRefreshToken("refresh_token");
        tokenResponse.setRole(RoleType.user);
        tokenResponse.setGrantType("Bearer");
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공")
        void loginSuccess() {
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(testMember.getEmail(), testMember.getPassword()));
            when(jwtTokenProvider.generateToken(any())).thenReturn(tokenResponse);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(memberService.findById(anyLong())).thenReturn(testMember);

            TokenResponse result = authService.login(loginRequest);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
        }

        @Test
        @DisplayName("로그인 실패: 비밀번호 불일치")
        void loginFailPasswordMismatch() {
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("비밀번호를 확인해주세요.");
        }

        @Test
        @DisplayName("로그인 실패: 사용자 미존재")
        void loginFailUserNotFound() {
            when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("소셜 로그인 테스트")
    class SocialLoginTest {
        @Test
        @DisplayName("소셜 로그인 성공")
        void socialLoginSuccess() {
            String email = "social@test.com";

            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(email, null));
            when(jwtTokenProvider.generateToken(any())).thenReturn(tokenResponse);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(memberService.findById(anyLong())).thenReturn(testMember);

            TokenResponse result = authService.socialLogin(email);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
        }

        @Test
        @DisplayName("소셜 로그인 실패: 사용자 미존재")
        void socialLoginFailUserNotFound() {
            when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            assertThatThrownBy(() -> authService.socialLogin("nonexistent@test.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("토큰 갱신 테스트")
    class RefreshTokenTest {

        @Test
        @DisplayName("토큰 갱신 성공")
        void refreshTokenSuccess() {
            String refreshToken = "valid_refresh_token";

            when(jwtTokenProvider.refreshAccessToken(anyString())).thenReturn(tokenResponse);
            when(jwtTokenProvider.getEmailFromToken(anyString())).thenReturn(testMember.getEmail());
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(memberService.findById(anyLong())).thenReturn(testMember);

            TokenResponse result = authService.refreshToken(refreshToken);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
        }

        @Test
        @DisplayName("토큰 갱신 실패: 잘못된 토큰")
        void refreshTokenFailInvalidToken() {
            String refreshToken = "invalid_refresh_token";

            when(jwtTokenProvider.refreshAccessToken(anyString())).thenThrow(new IllegalArgumentException("잘못된 토큰입니다."));

            assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("잘못된 토큰입니다.");
        }
    }

    @Nested
    @DisplayName("권한 추가 테스트")
    class AddAuthorityTest {

        @Test
        @DisplayName("권한 추가 성공")
        void addAuthoritySuccess() {
            when(memberService.findById(anyLong())).thenReturn(testMember);

            authService.addAuthority(1L, RoleType.admin);

            verify(memberService).findById(anyLong());
            verify(memberRepository).save(any(Member.class));
            assertThat(testMember.getRole()).isEqualTo(RoleType.admin);
        }

        @Test
        @DisplayName("권한 추가 실패: 사용자 미존재")
        void addAuthorityFailUserNotFound() {
            when(memberService.findById(anyLong())).thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            assertThatThrownBy(() -> authService.addAuthority(1L, RoleType.admin))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }
    }
}
