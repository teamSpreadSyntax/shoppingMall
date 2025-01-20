package home.project.service.member;

import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.LoginRequestDTO;
import home.project.dto.responseDTO.TokenResponse;
import home.project.repository.member.MemberRepository;
import home.project.service.util.JwtTokenProvider;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

        // Mock PasswordEncoder 동작 정의
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded_" + invocation.getArgument(0));

        // LoginRequestDTO 설정
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        // Member 설정
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail(testEmail);
        testMember.setPassword(passwordEncoder.encode(testPassword));
        testMember.setRole(RoleType.user);

        // GrantedAuthority 설정
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // UserDetails 설정
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testEmail);
        when(userDetails.getPassword()).thenReturn(testMember.getPassword());
        when(userDetails.getAuthorities()).thenReturn((List) authorities);

        // TokenResponse 설정
        tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("test.access.token");
        tokenResponse.setRefreshToken("test.refresh.token");
        tokenResponse.setRole(RoleType.user);
        tokenResponse.setGrantType("Bearer");
    }


    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("정상적인 로그인 요청시 성공한다")
        void loginSuccess() {
            // given
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(authenticationManager.authenticate(any())).thenReturn(
                    new UsernamePasswordAuthenticationToken(testMember.getEmail(), testMember.getPassword())
            );
            when(jwtTokenProvider.generateToken(any())).thenReturn(tokenResponse);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(memberService.findById(anyLong())).thenReturn(testMember);

            // when
            TokenResponse result = authService.login(loginRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
            assertThat(result.getRole()).isEqualTo(RoleType.user);
            verify(userDetailsService).loadUserByUsername(anyString());
            verify(memberService).findByEmail(anyString());
            verify(memberService).findById(anyLong());
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인시 실패한다")
        void loginFailWrongPassword() {
            // given
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("비밀번호를 확인해주세요.");
        }
    }

    @Nested
    @DisplayName("소셜 로그인 테스트")
    class SocialLoginTest {

        @Test
        @DisplayName("정상적인 소셜 로그인 요청시 성공한다")
        void socialLoginSuccess() {
            // given
            String email = "test@test.com";
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            when(authenticationManager.authenticate(any())).thenReturn(
                    new UsernamePasswordAuthenticationToken(email, "null")
            );
            when(jwtTokenProvider.generateToken(any())).thenReturn(tokenResponse);
            when(memberService.findByEmail(anyString())).thenReturn(testMember);
            when(memberService.findById(anyLong())).thenReturn(testMember);

            // when
            TokenResponse result = authService.socialLogin(email);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
            assertThat(result.getRole()).isEqualTo(RoleType.user);
            verify(memberService).findByEmail(anyString());
            verify(memberService).findById(anyLong());
        }
    }

    @Test
    @DisplayName("토큰 갱신 요청시 성공한다")
    void refreshTokenSuccess() {
        // given
        String refreshToken = "refresh.token";
        when(jwtTokenProvider.refreshAccessToken(anyString())).thenReturn(tokenResponse);
        when(jwtTokenProvider.getEmailFromToken(anyString())).thenReturn(testMember.getEmail());
        when(memberService.findByEmail(anyString())).thenReturn(testMember);
        when(memberService.findById(anyLong())).thenReturn(testMember);

        // when
        TokenResponse result = authService.refreshToken(refreshToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
        assertThat(result.getRole()).isEqualTo(RoleType.user);
        verify(memberService).findByEmail(anyString());
        verify(memberService).findById(anyLong());
    }

    @Test
    @DisplayName("권한 추가 요청시 성공한다")
    void addAuthoritySuccess() {
        // given
        when(memberService.findById(anyLong())).thenReturn(testMember);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // when
        authService.addAuthority(1L, RoleType.admin);

        // then
        verify(memberService).findById(anyLong());
        verify(memberRepository).save(any(Member.class));
        assertThat(testMember.getRole()).isEqualTo(RoleType.admin);
    }
}