/*
package home.project.service;

import home.project.domain.Member;
import home.project.domain.RoleType;
import home.project.dto.requestDTO.LoginRequestDTO;
import home.project.dto.responseDTO.RoleResponse;
import home.project.dto.responseDTO.TokenResponse;
import home.project.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

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

    private LoginRequestDTO loginRequestDTO;
    private Member member;
    private TokenResponse tokenResponse;
    private Pageable pageable;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loginRequestDTO = new LoginRequestDTO("test@example.com", "password");
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPassword("encodedPassword");
        member.setRole(RoleType.user);
        tokenResponse = new TokenResponse("accessToken", "refreshToken", "Bearer", RoleType.user);
        pageable = PageRequest.of(0, 5);
        authentication = new UsernamePasswordAuthenticationToken("test@example.com", "password");
    }

    @Nested
    class LoginTests {
        @Test
        void login_ValidCredentials_ReturnsTokenResponse() {
            UserDetails userDetails = mock(UserDetails.class);

            when(userDetailsService.loadUserByUsername(loginRequestDTO.getEmail())).thenReturn(userDetails);
            when(passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn(tokenResponse);
            when(memberService.findByEmail(loginRequestDTO.getEmail())).thenReturn(member);
            when(memberService.findById(member.getId())).thenReturn(member);

            TokenResponse result = authService.login(loginRequestDTO);

            assertEquals(tokenResponse, result);
            assertEquals(RoleType.user, result.getRole());
        }

        @Test
        void login_InvalidCredentials_ThrowsBadCredentialsException() {
            UserDetails userDetails = mock(UserDetails.class);

            when(userDetailsService.loadUserByUsername(loginRequestDTO.getEmail())).thenReturn(userDetails);
            when(passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDTO));
        }
    }

    @Nested
    class RefreshTokenTests {
        @Test
        void refreshToken_ValidToken_ReturnsNewTokenResponse() {
            when(jwtTokenProvider.refreshAccessToken("refreshToken")).thenReturn(tokenResponse);
            when(jwtTokenProvider.getEmailFromToken(tokenResponse.getAccessToken())).thenReturn("test@example.com");
            when(memberService.findByEmail("test@example.com")).thenReturn(member);
            when(memberService.findById(member.getId())).thenReturn(member);

            TokenResponse result = authService.refreshToken("refreshToken");

            assertEquals(tokenResponse, result);
            assertEquals(RoleType.user, result.getRole());
        }

        @Test
        void refreshToken_InvalidToken_ThrowsJwtException() {
            when(jwtTokenProvider.refreshAccessToken("invalidToken")).thenThrow(new JwtException("Invalid refresh token"));

            assertThrows(JwtException.class, () -> authService.refreshToken("invalidToken"));
        }
    }

    @Nested
    class AddAuthorityTests {
        @Test
        void addAuthority_ValidIdAndAuthority_AddsAuthority() {
            when(memberService.findById(1L)).thenReturn(member);

            authService.addAuthority(1L, RoleType.admin);

            verify(memberRepository).save(member);
            assertEquals(RoleType.admin, member.getRole());
        }

        @Test
        void addAuthority_InvalidId_ThrowsEntityNotFoundException() {
            when(memberService.findById(999L)).thenThrow(new RuntimeException("Member not found"));

            assertThrows(RuntimeException.class, () -> authService.addAuthority(999L, RoleType.admin));
        }
    }

    @Nested
    class RoleMessageTests {
        @Test
        void roleMessage_ValidIdAndAuthority_ReturnsMessage() {
            when(memberService.findById(1L)).thenReturn(member);

            String result = authService.roleMessage(1L, RoleType.admin);

            assertTrue(result.contains("중간 관리자 권한을 부여했습니다."));
        }

        @Test
        void roleMessage_InvalidId_ThrowsEntityNotFoundException() {
            when(memberService.findById(999L)).thenThrow(new RuntimeException("Member not found"));

            assertThrows(RuntimeException.class, () -> authService.roleMessage(999L, RoleType.admin));
        }
    }

    @Nested
    class CheckAuthorityTests {
        @Test
        void checkAuthority_ReturnsPageOfRoleResponses() {
            Page<Member> memberPage = new PageImpl<>(Arrays.asList(member));
            when(memberService.findAll(pageable)).thenReturn(memberPage);

            Page<RoleResponse> result = authService.checkAuthority(pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(member.getId(), result.getContent().get(0).getId());
            assertEquals(member.getRole(), result.getContent().get(0).getRole());
        }

        @Test
        void checkAuthority_EmptyPage_ReturnsEmptyPage() {
            Page<Member> emptyPage = new PageImpl<>(Collections.emptyList());
            when(memberService.findAll(pageable)).thenReturn(emptyPage);

            Page<RoleResponse> result = authService.checkAuthority(pageable);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class VerifyUserTests {
        @Test
        void verifyUser_ValidTokens_ReturnsTokenResponse() {
            doNothing().when(jwtTokenProvider).validateTokenResult("accessToken", "refreshToken");
            when(jwtTokenProvider.getEmailFromToken("accessToken")).thenReturn("test@example.com");
            when(memberService.findByEmail("test@example.com")).thenReturn(member);
            when(memberService.findById(member.getId())).thenReturn(member);

            TokenResponse result = authService.verifyUser("accessToken", "refreshToken");

            assertEquals("accessToken", result.getAccessToken());
            assertEquals("refreshToken", result.getRefreshToken());
            assertEquals(RoleType.user, result.getRole());
            assertEquals("Bearer", result.getGrantType());
        }

        @Test
        void verifyUser_InvalidTokens_ThrowsJwtException() {
            doThrow(new JwtException("Invalid token")).when(jwtTokenProvider).validateTokenResult("invalidAccess", "invalidRefresh");

            assertThrows(JwtException.class, () -> authService.verifyUser("invalidAccess", "invalidRefresh"));
        }
    }
}*/
