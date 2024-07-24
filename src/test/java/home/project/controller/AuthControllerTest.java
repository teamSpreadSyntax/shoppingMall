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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
            member.setPassword("encodedPassword");

            when(userDetailsService.loadUserByUsername(userDetailsDTO.getEmail())).thenReturn(userDetails);
            when(userDetails.getPassword()).thenReturn(member.getPassword());
            when(passwordEncoder.matches(userDetailsDTO.getPassword(), member.getPassword())).thenReturn(true);

            TokenDto tokenDto = new TokenDto("Bearer", "accessToken", "refreshToken");
            when(tokenProvider.generateToken(any())).thenReturn(tokenDto);

            mockMvc.perform(post("/api/loginToken/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"test@example.com\", \"password\": \"password\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.grantType").value("Bearer"))
                    .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"))
                    .andExpect(jsonPath("$.responseMessage").value(userDetails.getUsername() + "(으)로 로그인에 성공했습니다."))
                    .andExpect(jsonPath("$.status").value(200));

            verify(passwordEncoder).matches(userDetailsDTO.getPassword(), member.getPassword());
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
                    .andExpect(jsonPath("$.result.successMessage").value(member.getEmail()+"님 이용해주셔서 감사합니다."))
                    .andExpect(jsonPath("$.responseMessage").value("로그아웃되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));

        }
    }

    @Nested
    class AuthorityTests {
        @Test
        void addAuthority_CenterRole_SuccessfullyAssignsRole() throws Exception {

            String authority = "admin";
            String memberName = "홍길동";

            Role role = new Role();
            role.setId(memberId);
            role.setRole("center");

            Member member = new Member();
            member.setId(memberId);
            member.setName(memberName);

            when(roleService.findById(memberId)).thenReturn(Optional.of(role));
            when(memberService.findById(memberId)).thenReturn(Optional.of(member));
            when(roleService.update(role)).thenReturn(Optional.of(role));

            mockMvc.perform(post("/api/loginToken/authorization")
                            .param("memberId", memberId.toString())
                            .param("authority", authority))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.role").value(authority))
                    .andExpect(jsonPath("$.responseMessage").value(memberName + "(id : " + memberId + ")님에게 중간 관리자 권한을 부여했습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class AuthoritiesTests {
        @Test
        void checkAuthority_ReturnsPagedUserRoleList() throws Exception {
            int page = 1;
            int size = 5;
            long totalElements = 10L;

            Member member1 = new Member();
            member1.setId(1L);
            member1.setName("User1");
            member1.setEmail("user1@example.com");

            Member member2 = new Member();
            member2.setId(2L);
            member2.setName("User2");
            member2.setEmail("user2@example.com");

            List<Member> members = Arrays.asList(member1, member2);
            Page<Member> memberPage = new PageImpl<>(members, PageRequest.of(page - 1, size), totalElements);

            Role role1 = new Role();
            role1.setId(1L);
            role1.setRole("user");

            Role role2 = new Role();
            role2.setId(2L);
            role2.setRole("admin");

            when(memberService.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(roleService.findById(1L)).thenReturn(Optional.of(role1));
            when(roleService.findById(2L)).thenReturn(Optional.of(role2));

            mockMvc.perform(get("/api/loginToken/authorities")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.totalCount").value(totalElements))
                    .andExpect(jsonPath("$.result.page").value(page - 1))
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(members.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(1))
                    .andExpect(jsonPath("$.result.content[0].role").value("user"))
                    .andExpect(jsonPath("$.result.content[0].name").value("User1"))
                    .andExpect(jsonPath("$.result.content[1].id").value(2))
                    .andExpect(jsonPath("$.result.content[1].role").value("admin"))
                    .andExpect(jsonPath("$.result.content[1].name").value("User2"))
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원별 권한 목록입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

    }
}
