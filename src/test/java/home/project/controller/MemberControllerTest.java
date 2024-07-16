package home.project.controller;

import home.project.SecurityConfig;
import home.project.domain.*;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.service.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@Import({SecurityConfig.class})
@ExtendWith(MockitoExtension.class)
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
    private UserDetailsService userDetailsService; //이걸 지우면 왜 안될까

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    private MemberDTOWithoutId memberDTO;
    private Member member;
    private List<Member> members;
    private Page<Member> memberPage;
    private Page<MemberDTOWithoutPw> memberDtoPage;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        memberDTO = new MemberDTOWithoutId();
        memberDTO.setEmail("test@example.com");
        memberDTO.setPassword("1111");
        memberDTO.setName("홍길동");
        memberDTO.setPhone("010-1111-1111");

        member = new Member();
        member.setId(1L);
        member.setEmail(memberDTO.getEmail());
        member.setPassword(memberDTO.getPassword());
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());

        Member member1 = new Member();
        member1.setId(2L);
        member1.setEmail("test2@example.com");
        member1.setPassword("2222");
        member1.setName("김길동");
        member1.setPhone("010-2222-2222");

        members = Arrays.asList(member, member1);
        pageable = PageRequest.of(1, 5);
        memberPage = new PageImpl<>(members, pageable, members.size());
        memberDtoPage = memberPage.map(member -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone()));



    }

    @Nested
    class createMemberTest {
        @Test
        public void createMember_Success_ReturnsTokens() throws Exception {

            Role role = Mockito.mock(Role.class);
            role.setId(1L);
            role.setRole("user");

            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(memberService.findByEmail(memberDTO.getEmail())).thenReturn(Optional.of(member));
            when(memberService.findById(1L)).thenReturn(Optional.of(member));
            verify(role).setId(member.getId());
            doNothing().when(memberService).join(any(Member.class));
            doNothing().when(roleService).join(any(Role.class));
            when(jwtTokenProvider.generateToken(any())).thenReturn(new TokenDto("bearer", "accessToken", "refreshToken"));


            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"test@example.com\", \"password\": \"1111\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.accessToken").exists())
                    .andExpect(jsonPath("$.result.refreshToken").exists())
                    .andExpect(jsonPath("$.result.message").value("회원가입이 성공적으로 완료되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));;

        }
    }

    @Nested
    class findMemberByIdTest {
        @Test
        public void findMemberById_Existing_ReturnsMemberInfo() throws Exception {
            long memberId = 1L;

            when(memberService.findById(memberId)).thenReturn(Optional.of(member));

            mockMvc.perform(get("/api/member/member")
                            .param("memberId", String.valueOf(memberId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(memberId))
                    .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.result.name").value(member.getName()))
                    .andExpect(jsonPath("$.result.phone").value(member.getPhone()))
                    .andExpect(jsonPath("$.responseMessage").value(memberId + "(으)로 가입된 회원정보입니다"))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class findAllMembersTests {
        @Test
        public void findAllMembers_Existing_ReturnsMembersPage() throws Exception {

            when(memberService.findAll(any(Pageable.class))).thenReturn(memberPage);

            mockMvc.perform(get("/api/member/members")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(members.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(memberDtoPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].email").value(memberDtoPage.getContent().get(0).getEmail()))
                    .andExpect(jsonPath("$.result.content[0].name").value(memberDtoPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].phone").value(memberDtoPage.getContent().get(0).getPhone()))
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void findAllMembers_RequestOverPage_ReturnsEmptyPage() throws Exception {
            when(memberService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

            mockMvc.perform(get("/api/member/members")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result").exists())
                        .andExpect(jsonPath("$.result.content").isArray())
                        .andExpect(jsonPath("$.result.content.length()").value(0))
                        .andExpect(jsonPath("$.responseMessage").value("전체 회원입니다."))
                        .andExpect(jsonPath("$.status").value(200));

        }
    }

    @Nested
    class searchMembersTest {
        @Test
        public void searchMembers_MatchingCriteria_ReturnsMatchingMembers() throws Exception {
            String name = "홍길동";
            String email = "test@example.com";
            String phone = "010-1111-1111";
            String content = "search content";

            when(memberService.findMembers(any(), any(), any(), any(), any(Pageable.class))).thenReturn(memberPage);

            mockMvc.perform(get("/api/member/search")
                            .param("name", name)
                            .param("email", email)
                            .param("phone", phone)
                            .param("content", content)
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(members.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(memberPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].email").value(memberPage.getContent().get(0).getEmail()))
                    .andExpect(jsonPath("$.result.content[0].password").value(memberPage.getContent().get(0).getPassword()))
                    .andExpect(jsonPath("$.result.content[0].name").value(memberPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].phone").value(memberPage.getContent().get(0).getPhone()))
                    .andExpect(jsonPath("$.responseMessage").value("검색 결과입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class updateMemberTest {
        @Test
        public void updateMember_Success_ReturnsUpdatedMemberInfo() throws Exception {

            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(memberService.update(any(Member.class))).thenReturn(Optional.of(member));

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"email\": \"test@example.com\", \"password\": \"1111\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(member.getId()))
                    .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.result.name").value(member.getName()))
                    .andExpect(jsonPath("$.result.phone").value(member.getPhone()))
                    .andExpect(jsonPath("$.responseMessage").value("회원 정보가 수정되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class deleteMemberTest {
        @Test
        public void deleteMember_Existing_DeletesMemberAndReturnsSuccessMessage() throws Exception {
            long memberId = 1L;

            doNothing().when(memberService).deleteById(memberId);

            mockMvc.perform(delete("/api/member/delete")
                            .param("memberId", String.valueOf(memberId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.successMessage").value(memberId + "님의 계정이 삭제되었습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("회원 삭제 성공"))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }
}
