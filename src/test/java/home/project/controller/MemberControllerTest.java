package home.project.controller;

import home.project.SecurityConfig;
import home.project.domain.Member;
import home.project.domain.MemberDTOWithoutId;
import home.project.domain.Role;
import home.project.domain.TokenDto;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.service.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@Import({SecurityConfig.class})
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private ValidationCheck validationCheck;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMember_Success() throws Exception {
        MemberDTOWithoutId memberDTO = new MemberDTOWithoutId();
        memberDTO.setEmail("test@example.com");
        memberDTO.setPassword("123456");
        memberDTO.setName("강민서");
        memberDTO.setPhone("010-1234-5678");

        when(validationCheck.validationChecks(bindingResult)).thenReturn(null);

        Member member = new Member();
        member.setId(1L);
        member.setEmail(memberDTO.getEmail());
        member.setPassword(memberDTO.getPassword());
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());

        Role role = new Role();
        role.setId(1L);
        role.setRole("user");

        when(memberService.findByEmail(memberDTO.getEmail())).thenReturn(Optional.empty());
        when(roleService.findById(1L)).thenReturn(Optional.of(role));
        doNothing().when(memberService).join(any(Member.class));
        when(jwtTokenProvider.generateToken(any())).thenReturn(new TokenDto("bearer", "accessToken", "refreshToken"));

        mockMvc.perform(post("/api/member/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"password\": \"123456\", \"name\": \"강민서\", \"phone\": \"010-1234-5678\" }"))
                .andExpect(status().isOk());
    }
}
