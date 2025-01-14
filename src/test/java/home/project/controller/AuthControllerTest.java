/*
package home.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.config.security.SecurityConfig;
import home.project.domain.*;
import home.project.dto.responseDTO.TokenDto;
import home.project.dto.UserDetailsDTO;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.response.CustomOptionalResponseBody;
import home.project.response.CustomOptionalResponseEntity;
import home.project.service.security.JwtTokenProvider;
import home.project.service.member.MemberService;
import home.project.service.RoleService;
import home.project.service.validation.ValidationCheck;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class})
@ExtendWith({MockitoExtension.class})
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserDetails userDetails;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ValidationCheck validationCheck;

    @MockBean
    private RoleService roleService;

    private ObjectMapper objectMapper;
    private UserDetailsDTO validUserDetailsDTO;
    private Member testMember;
    private Role testRole;
    private TokenDto testTokenDto;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        validUserDetailsDTO = new UserDetailsDTO();
        validUserDetailsDTO.setEmail("test@example.com");
        validUserDetailsDTO.setPassword("ValidPassword123!");

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");
        testMember.setName("Test User");
        testMember.setPassword("encodedPassword");

        testRole = new Role();
        testRole.setId(1L);
        testRole.setRole("testRole");

        testTokenDto = new TokenDto("Bearer", "testAccessToken", "testRefreshToken", "testRole");
    }


    @Nested
    class LoginTests {

        @Test
        void login_ValidCredentials_ReturnsToken() throws Exception {
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn(validUserDetailsDTO.getEmail());
            when(userDetails.getPassword()).thenReturn("encodedPassword");
            when(userDetailsService.loadUserByUsername(validUserDetailsDTO.getEmail())).thenReturn(userDetails);
            when(passwordEncoder.matches(validUserDetailsDTO.getPassword(), userDetails.getPassword())).thenReturn(true);
            when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
            when(tokenProvider.generateToken(any())).thenReturn(testTokenDto);
            when(memberService.findByEmail(validUserDetailsDTO.getEmail())).thenReturn(Optional.of(testMember));
            when(roleService.findById(testMember.getId())).thenReturn(Optional.of(testRole));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUserDetailsDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.grantType").value("Bearer"))
                    .andExpect(jsonPath("$.result.accessToken").value("testAccessToken"))
                    .andExpect(jsonPath("$.result.refreshToken").value("testRefreshToken"))
                    .andExpect(jsonPath("$.result.role").value("testRole"))
                    .andExpect(jsonPath("$.responseMessage").value(validUserDetailsDTO.getEmail() + "(으)로 로그인에 성공했습니다."))
                    .andExpect(jsonPath("$.status").value(200));

            verify(passwordEncoder).matches(validUserDetailsDTO.getPassword(), userDetails.getPassword());
            verify(memberService).findByEmail(validUserDetailsDTO.getEmail());
            verify(roleService).findById(testMember.getId());
        }

        @Test
        void login_EmptyInput_ReturnsBadRequest() throws Exception {
            UserDetailsDTO invalidEmailDTO = new UserDetailsDTO();
            invalidEmailDTO.setEmail("");
            invalidEmailDTO.setPassword("ValidPassword123!");

            Map<String, String> errors = new HashMap<>();
            errors.put("email", "이메일을 입력해주세요.");
            errors.put("password", "비밀번호를 입력해주세요.");
            when(validationCheck.validationChecks(any())).thenReturn(
                    new CustomOptionalResponseEntity<>(new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST)
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEmailDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.email").value("이메일을 입력해주세요."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호를 입력해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void login_InvalidInput_ReturnsBadRequest() throws Exception {
            UserDetailsDTO invalidEmailDTO = new UserDetailsDTO();
            invalidEmailDTO.setEmail("invalid-email");
            invalidEmailDTO.setPassword("invalid-password");

            Map<String, String> errors = new HashMap<>();
            errors.put("email", "올바른 이메일 형식이 아닙니다.");
            errors.put("password", "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다.");
            when(validationCheck.validationChecks(any())).thenReturn(
                    new CustomOptionalResponseEntity<>(new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST)
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEmailDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.email").value("올바른 이메일 형식이 아닙니다."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void login_NonExistingUser_ReturnsNotFound() throws Exception {
            when(userDetailsService.loadUserByUsername(validUserDetailsDTO.getEmail()))
                    .thenThrow(new UsernameNotFoundException(validUserDetailsDTO.getEmail() + "(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUserDetailsDTO)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value(validUserDetailsDTO.getEmail() + "(으)로 등록된 회원이 없습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("해당 회원이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(401));
        }

        @Test
        void login_InvalidPassword_ReturnsBadCredentials() throws Exception {
            when(userDetailsService.loadUserByUsername(validUserDetailsDTO.getEmail())).thenReturn(mock(UserDetails.class));
            when(passwordEncoder.matches(any(), any())).thenReturn(false);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUserDetailsDTO)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("비밀번호를 확인해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("비밀번호가 틀립니다."))
                    .andExpect(jsonPath("$.status").value(401));
        }
    }

    @Nested
    class RefreshTokenTests {

        @Test
        void refreshToken_ValidTokens_ReturnsNewTokens() throws Exception {
            String expiredAccessToken = "expiredAccessToken";
            String refreshToken = "validRefreshToken";
            TokenDto newTokenDto = new TokenDto("Bearer", "newAccessToken", "newRefreshToken", "testRole");

            when(tokenProvider.refreshAccessToken(refreshToken)).thenReturn(newTokenDto);
            when(tokenProvider.getEmailFromToken("newAccessToken")).thenReturn("test@example.com");
            when(memberService.findByEmail("test@example.com")).thenReturn(Optional.of(testMember));
            when(roleService.findById(testMember.getId())).thenReturn(Optional.of(testRole));

            mockMvc.perform(post("/api/auth/refresh")
                            .param("expiredAccessToken", expiredAccessToken)
                            .param("refreshToken", refreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.grantType").value("Bearer"))
                    .andExpect(jsonPath("$.result.accessToken").value("newAccessToken"))
                    .andExpect(jsonPath("$.result.refreshToken").value("newRefreshToken"))
                    .andExpect(jsonPath("$.result.role").value("testRole"))
                    .andExpect(jsonPath("$.responseMessage").value("토큰이 성공적으로 갱신되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));

            verify(tokenProvider).refreshAccessToken(refreshToken);
            verify(tokenProvider).getEmailFromToken("newAccessToken");
            verify(memberService).findByEmail("test@example.com");
            verify(roleService).findById(testMember.getId());
        }

        @Test
        void refreshToken_InvalidTokens_ReturnsUnauthorized() throws Exception {
            String refreshToken = "invalidRefreshToken";

            when(tokenProvider.refreshAccessToken(refreshToken))
                    .thenThrow(new JwtException("유효하지 않은 Refresh token입니다."));

            mockMvc.perform(post("/api/auth/refresh")
                            .param("refreshToken", refreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("유효하지 않은 Refresh token입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("유효하지 않은 토큰입니다."))
                    .andExpect(jsonPath("$.status").value(401));

            verify(tokenProvider).refreshAccessToken(refreshToken);
        }

        @Test
        void refreshToken_ExpiredTokens_ReturnsUnauthorized() throws Exception {
            String refreshToken = "invalidRefreshToken";

            when(tokenProvider.refreshAccessToken(refreshToken))
                    .thenThrow(new JwtException("만료된 Refresh token입니다. 다시 로그인 해주세요."));

            mockMvc.perform(post("/api/auth/refresh")
                            .param("refreshToken", refreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("만료된 Refresh token입니다. 다시 로그인 해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("유효하지 않은 토큰입니다."))
                    .andExpect(jsonPath("$.status").value(401));

            verify(tokenProvider).refreshAccessToken(refreshToken);
        }
    }

    @Nested
    class LogoutTests {
        @Test
        void logout_ExistingMember_ReturnsSuccessMessage() throws Exception {
            when(memberService.findById(1L)).thenReturn(Optional.of(testMember));
            when(roleService.findById(1L)).thenReturn(Optional.of(testRole));

            mockMvc.perform(post("/api/auth/logout")
                            .param("memberId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.successMessage").value(testMember.getEmail() + "님 이용해주셔서 감사합니다."))
                    .andExpect(jsonPath("$.responseMessage").value("로그아웃되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));

            verify(memberService).logout(1L);
            verify(roleService).update(any(Role.class));
        }

        @Test
        void logout_NonExistingMember_ReturnsNotFound() throws Exception {
            when(memberService.findById(99L)).thenThrow(new IdNotFoundException("99(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(post("/api/auth/logout")
                            .param("memberId", "99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 회원이 없습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @WithMockUser(roles = {"CENTER"})
    class AuthorityTests {
        @Test
        void addAuthority_ValidInput_ReturnsSuccessMessage() throws Exception {
            when(roleService.findById(1L)).thenReturn(Optional.of(testRole));
            when(memberService.findById(1L)).thenReturn(Optional.of(testMember));
            when(roleService.update(any(Role.class))).thenReturn(Optional.of(testRole));

            mockMvc.perform(post("/api/auth/authorization")
                            .param("memberId", "1")
                            .param("authority", "admin"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.role").value("admin"))
                    .andExpect(jsonPath("$.responseMessage").value(testMember.getName() + "(id : 1)님에게 중간 관리자 권한을 부여했습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        void addAuthority_NonExistingMember_ReturnsNotFound() throws Exception {
            when(roleService.findById(99L)).thenThrow(new IdNotFoundException("99(으)로 등록된 회원이 없습니다."));
            when(memberService.findById(99L)).thenThrow(new IdNotFoundException("99(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(post("/api/auth/authorization")
                            .param("memberId", "99")
                            .param("authority", "admin"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorMessage").value("99(으)로 등록된 회원이 없습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @WithMockUser(roles = {"CENTER"})
    class AuthoritiesTests {
        @Test
        void checkAuthority_ValidInput_ReturnsUserRolePage() throws Exception {
            List<Member> members = Arrays.asList(testMember);
            Page<Member> memberPage = new PageImpl<>(members, PageRequest.of(0, 5), 1);

            when(memberService.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(roleService.findById(anyLong())).thenReturn(Optional.of(testRole));

            mockMvc.perform(get("/api/auth/authorities")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.totalCount").value(1))
                    .andExpect(jsonPath("$.result.page").value(0))
                    .andExpect(jsonPath("$.result.content[0].id").value(testMember.getId()))
                    .andExpect(jsonPath("$.result.content[0].role").value(testRole.getRole()))
                    .andExpect(jsonPath("$.result.content[0].name").value(testMember.getName()))
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원별 권한 목록입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void checkAuthority_EmptyPage_ReturnsEmptyPage() throws Exception {
            when(memberService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

            mockMvc.perform(get("/api/auth/authorities")
                            .param("page", "1000")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.totalCount").value(0))
                    .andExpect(jsonPath("$.result.page").value(0))
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(0))
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원별 권한 목록입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        void checkAuthority_NegativePageNumber_ReturnsBadRequest() throws Exception {
            when(memberService.findAll(any(Pageable.class))).thenThrow(new IllegalArgumentException("Page index must not be less than zero"));

            mockMvc.perform(get("/api/auth/authorities")
                            .param("page", "-1")
                            .param("size", "5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.errorMessage").value("Page index must not be less than zero"))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    class VerifyUserTests {

        @Test
        void verifyUser_ValidTokens_ReturnsValidatedTokens() throws Exception {
            String validAccessToken = "validAccessToken";
            String validRefreshToken = "validRefreshToken";

            doNothing().when(tokenProvider).validateTokenResult(validAccessToken, validRefreshToken);
            when(tokenProvider.getEmailFromToken(validAccessToken)).thenReturn("test@example.com");
            when(memberService.findByEmail("test@example.com")).thenReturn(Optional.of(testMember));
            when(roleService.findById(testMember.getId())).thenReturn(Optional.of(testRole));

            mockMvc.perform(get("/api/auth/verify")
                            .param("accessToken", validAccessToken)
                            .param("refreshToken", validRefreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.grantType").value("Bearer"))
                    .andExpect(jsonPath("$.result.accessToken").value(validAccessToken))
                    .andExpect(jsonPath("$.result.refreshToken").value(validRefreshToken))
                    .andExpect(jsonPath("$.result.role").value(testRole.getRole()))
                    .andExpect(jsonPath("$.responseMessage").value("토큰이 검증되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));

            verify(tokenProvider).validateTokenResult(validAccessToken, validRefreshToken);
            verify(tokenProvider).getEmailFromToken(validAccessToken);
            verify(memberService).findByEmail("test@example.com");
            verify(roleService).findById(testMember.getId());
        }

        @Test
        void verifyUser_InvalidAccessTokens_ReturnsUnauthorized() throws Exception {
            String invalidAccessToken = "invalidAccessToken";
            String validRefreshToken = "validRefreshToken";

            doThrow(new JwtException("유효하지 않은 Access token입니다."))
                    .when(tokenProvider).validateTokenResult(invalidAccessToken, validRefreshToken);

            mockMvc.perform(get("/api/auth/verify")
                            .param("accessToken", invalidAccessToken)
                            .param("refreshToken", validRefreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("유효하지 않은 Access token입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("유효하지 않은 토큰입니다."))
                    .andExpect(jsonPath("$.status").value(401));

            verify(tokenProvider).validateTokenResult(invalidAccessToken, validRefreshToken);
        }

        @Test
        void verifyUser_ExpiredAccessTokens_ReturnsUnauthorized() throws Exception {
            String invalidAccessToken = "invalidAccessToken";
            String validRefreshToken = "validRefreshToken";

            doThrow(new JwtException("만료된 Access token입니다. 다시 로그인 해주세요."))
                    .when(tokenProvider).validateTokenResult(invalidAccessToken, validRefreshToken);

            mockMvc.perform(get("/api/auth/verify")
                            .param("accessToken", invalidAccessToken)
                            .param("refreshToken", validRefreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("만료된 Access token입니다. 다시 로그인 해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("유효하지 않은 토큰입니다."))
                    .andExpect(jsonPath("$.status").value(401));

            verify(tokenProvider).validateTokenResult(invalidAccessToken, validRefreshToken);
        }

        @Test
        void verifyUser_InvalidRefreshTokens_ReturnsUnauthorized() throws Exception {
            String validAccessToken = "validAccessToken";
            String invalidRefreshToken = "invalidRefreshToken";

            doThrow(new JwtException("유효하지 않은 Refresh token입니다."))
                    .when(tokenProvider).validateTokenResult(validAccessToken, invalidRefreshToken);

            mockMvc.perform(get("/api/auth/verify")
                            .param("accessToken", validAccessToken)
                            .param("refreshToken", invalidRefreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("유효하지 않은 Refresh token입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("유효하지 않은 토큰입니다."))
                    .andExpect(jsonPath("$.status").value(401));

            verify(tokenProvider).validateTokenResult(validAccessToken, invalidRefreshToken);
        }

        @Test
        void verifyUser_ExpiredRefreshTokens_ReturnsUnauthorized() throws Exception {
            String validAccessToken = "validAccessToken";
            String invalidRefreshToken = "invalidRefreshToken";

            doThrow(new JwtException("만료된 Refresh token입니다. 다시 로그인 해주세요."))
                    .when(tokenProvider).validateTokenResult(validAccessToken, invalidRefreshToken);

            mockMvc.perform(get("/api/auth/verify")
                            .param("accessToken", validAccessToken)
                            .param("refreshToken", invalidRefreshToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("만료된 Refresh token입니다. 다시 로그인 해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("유효하지 않은 토큰입니다."))
                    .andExpect(jsonPath("$.status").value(401));

            verify(tokenProvider).validateTokenResult(validAccessToken, invalidRefreshToken);
        }

    }

}
*/
