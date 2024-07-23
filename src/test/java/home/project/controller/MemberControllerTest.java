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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.*;

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

        Role role = new Role();
        role.setId(1L);
        role.setRole("user");
        member.setRole(role);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setRole("user");
        member1.setRole(role2);

        members = Arrays.asList(member, member1);
        pageable = PageRequest.of(1, 5);
        memberPage = new PageImpl<>(members, pageable, members.size());
        memberDtoPage = memberPage.map(member -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), member.getRole().getRole()));



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
            when(roleService.findById(anyLong())).thenReturn(Optional.of(role));

            doNothing().when(memberService).join(any(Member.class));
            doNothing().when(roleService).join(any(Role.class));
            when(jwtTokenProvider.generateToken(any())).thenReturn(new TokenDto("bearer", "accessToken", "refreshToken"));


            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"test@example.com\", \"password\": \"1111\", \"passwordConfirm\": \"1111\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.accessToken").exists())
                    .andExpect(jsonPath("$.result.refreshToken").exists())
                    .andExpect(jsonPath("$.result.successMessage").value("회원가입이 성공적으로 완료되었습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("회원가입 성공"))
                    .andExpect(jsonPath("$.status").value(200));;

        }

        @Test
        public void createMember_InvalidPasswordConfirm_ReturnsBadRequest() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"test@example.com\", \"password\": \"1111\", \"passwordConfirm\": \"wrong\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.result.errorMessage").value("비밀번호와 비밀번호 확인이 일치하지 않습니다."));
        }

        @Test
        public void createMember_InvalidEmail_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "유효하지 않은 이메일 형식입니다.");

            when(validationCheck.validationChecks(bindingResult)).thenReturn(new CustomOptionalResponseEntity<>(
                    new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            ));

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"invalid-email\", \"password\": \"1111\", \"passwordConfirm\": \"1111\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.result.email").value("유효하지 않은 이메일 형식입니다."));
        }
    }

    @Nested
    class findMemberByIdTest {
        @Test
        public void findMemberById_Existing_ReturnsMemberInfo() throws Exception {
            long memberId = 1L;

            when(memberService.findById(memberId)).thenReturn(Optional.of(member));
            when(roleService.findById(memberId)).thenReturn(Optional.of(member.getRole()));


            mockMvc.perform(get("/api/member/member")
                            .param("memberId", String.valueOf(memberId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(memberId))
                    .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.result.name").value(member.getName()))
                    .andExpect(jsonPath("$.result.phone").value(member.getPhone()))
                    .andExpect(jsonPath("$.result.role").value(member.getRole().getRole()))
                    .andExpect(jsonPath("$.responseMessage").value(memberId + "(으)로 가입된 회원정보입니다"))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void findMemberById_NonExisting_ReturnsNotFound() throws Exception {
            long memberId = 99L;

            when(memberService.findById(memberId)).thenThrow(new IllegalArgumentException(memberId + "(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(get("/api/member/member")
                            .param("memberId", String.valueOf(memberId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.result.errorMessage").value(memberId + "(으)로 등록된 회원이 없습니다."));
        }

    }

    @Nested
    class findAllMembersTests {
        @Test
        public void findAllMembers_Existing_ReturnsMembersPage() throws Exception {

            when(memberService.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));


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
                    .andExpect(jsonPath("$.result.content[0].role").exists())
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
            when(memberService.findMembers(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(memberPage);
            when(roleService.findById(anyLong())).thenReturn(Optional.of(member.getRole()));

            StringBuilder expectedMessage = new StringBuilder("검색 키워드 : ");
            expectedMessage.append(member.getName()).append(", ")
                    .append(member.getEmail()).append(", ")
                    .append(member.getPhone()).append(", ")
                    .append(member.getRole().getRole()).append(", ")
                    .append("search content");

            mockMvc.perform(get("/api/member/search")
                            .param("name", member.getName())
                            .param("email", member.getEmail())
                            .param("phone", member.getPhone())
                            .param("role", member.getRole().getRole())
                            .param("content", "search content")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(members.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(memberPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].email").value(memberPage.getContent().get(0).getEmail()))
                    .andExpect(jsonPath("$.result.content[0].name").value(memberPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].phone").value(memberPage.getContent().get(0).getPhone()))
                    .andExpect(jsonPath("$.result.content[0].role").value(memberPage.getContent().get(0).getRole().getRole()))
                    .andExpect(jsonPath("$.responseMessage").value(expectedMessage.toString()))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void searchMembers_NoKeywords_ReturnsAllMembers() throws Exception {
            when(memberService.findMembers(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(memberPage);
            when(roleService.findById(anyLong())).thenReturn(Optional.of(member.getRole()));

            mockMvc.perform(get("/api/member/search")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(members.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(memberPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].email").value(memberPage.getContent().get(0).getEmail()))
                    .andExpect(jsonPath("$.result.content[0].name").value(memberPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].phone").value(memberPage.getContent().get(0).getPhone()))
                    .andExpect(jsonPath("$.result.content[0].role").value(memberPage.getContent().get(0).getRole().getRole()))
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
    }

    @Nested
    class updateMemberTest {
        @Test
        public void updateMember_Success_ReturnsUpdatedMemberInfo() throws Exception {

            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(memberService.update(any(Member.class))).thenReturn(Optional.of(member));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));


            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"email\": \"test@example.com\", \"password\": \"1111\", \"passwordConfirm\": \"1111\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(member.getId()))
                    .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.result.name").value(member.getName()))
                    .andExpect(jsonPath("$.result.phone").value(member.getPhone()))
                    .andExpect(jsonPath("$.result.role").value(member.getRole().getRole()))
                    .andExpect(jsonPath("$.responseMessage").value("회원 정보가 수정되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        public void updateMember_PasswordMismatch_ReturnsBadRequest() throws Exception {
            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"email\": \"test@example.com\", \"password\": \"1111\", \"passwordConfirm\": \"wrong\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.result.errorMessage").value("비밀번호와 비밀번호 확인이 일치하지 않습니다."));
        }
        @Test
        public void updateMember_InvalidEmail_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "유효하지 않은 이메일 형식입니다.");

            when(validationCheck.validationChecks(bindingResult)).thenReturn(new CustomOptionalResponseEntity<>(
                    new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            ));

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"email\": \"invalid-email\", \"password\": \"1111\", \"passwordConfirm\": \"1111\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.result.email").value("유효하지 않은 이메일 형식입니다."));
        }
    }

    @Nested
    class deleteMemberTest {
        @Test
        public void deleteMember_Existing_DeletesMemberAndReturnsSuccessMessage() throws Exception {
            long memberId = 1L;
            String email = "test@example.com";

            when(memberService.findById(memberId)).thenReturn(Optional.of(member));
            doNothing().when(memberService).deleteById(memberId);

            mockMvc.perform(delete("/api/member/delete")
                            .param("memberId", String.valueOf(memberId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.successMessage").value(email + "(id:" + memberId + ")님의 계정이 삭제되었습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("회원 삭제 성공"))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void deleteMember_NonExisting_ReturnsNotFound() throws Exception {
            long memberId = 99L;

            when(memberService.findById(memberId)).thenThrow(new IllegalArgumentException(memberId + "(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(delete("/api/member/delete")
                            .param("memberId", String.valueOf(memberId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.result.errorMessage").value(memberId + "(으)로 등록된 회원이 없습니다."));
        }
    }
}
