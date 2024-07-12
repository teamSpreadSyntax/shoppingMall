package home.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.SecurityConfig;
import home.project.domain.Member;
import home.project.domain.Role;
import home.project.domain.TokenDto;
import home.project.domain.UserDetailsDTO;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.service.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private Long memberId;

    @BeforeEach
    public void setUp() {
        memberId = 1L;
    }

    @Nested
    class LoginTests {
        @Test
        void login_ValidCredentials_ReturnsToken() throws Exception {
            UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
            userDetailsDTO.setEmail("test@example.com");
            userDetailsDTO.setPassword("password");

            Member member = new Member();
            member.setEmail(userDetailsDTO.getEmail());
            member.setPassword(passwordEncoder.encode(userDetailsDTO.getPassword()));

            when(userDetailsService.loadUserByUsername(userDetailsDTO.getEmail())).thenReturn(userDetails);
            when(passwordEncoder.matches(userDetailsDTO.getPassword(), member.getPassword())).thenReturn(true);

            TokenDto tokenDto = new TokenDto("Bearer", "accessToken", "refreshToken");
            when(tokenProvider.generateToken(any())).thenReturn(tokenDto);
            System.out.println(userDetailsDTO.getEmail());
            mockMvc.perform(post("/api/loginToken/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"test@example.com\", \"password\": \"password\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.grantType").value("Bearer"))
                    .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"))
                    .andExpect(jsonPath("$.responseMessage").value(userDetails.getUsername() + "로 로그인에 성공하였습니다"))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class LogoutTests {
        @Test
        void logout_ExistingMember_ReturnsSuccessMessage() throws Exception {

            Member member = new Member();
            member.setId(memberId);
            member.setEmail("test@example.com");

            Role role = new Role();
            role.setId(memberId);
            role.setRole("ROLE_USER");

            when(memberService.findById(memberId)).thenReturn(Optional.of(member));
            when(roleService.findById(memberId)).thenReturn(Optional.of(role));

            mockMvc.perform(post("/api/loginToken/logout")
                            .param("memberId", memberId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(member.getEmail()))
                    .andExpect(jsonPath("$.responseMessage").value("로그아웃에 성공하였습니다"))
                    .andExpect(jsonPath("$.status").value(200));

        }
    }

    @Nested
    class AuthorityTests {
        @Test
        void addAuthority_CenterRole_SuccessfullyAssignsRole() throws Exception {

            String authority = "admin";

            Role role = new Role();
            role.setId(memberId);
            role.setRole("center");

            when(roleService.findById(memberId)).thenReturn(Optional.of(role));
            when(roleService.update(role)).thenReturn(Optional.of(role));

            mockMvc.perform(post("/api/loginToken/authority")
                            .param("memberId", memberId.toString())
                            .param("authority", authority))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.role").value(authority))
                    .andExpect(jsonPath("$.responseMessage").value(memberId + "에게 관리자 권한을 부여했습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }
}
