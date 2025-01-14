/*
package home.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.config.security.SecurityConfig;
import home.project.domain.*;
import home.project.dto.*;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.response.CustomOptionalResponseBody;
import home.project.response.CustomOptionalResponseEntity;
import home.project.service.security.JwtTokenProvider;
import home.project.service.member.MemberService;
import home.project.service.RoleService;
import home.project.service.validation.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    private MemberDTOWithoutId memberDTOWithoutId;
    private Member member;
    private Member updatedMember;
    private List<Member> memberList;
    private Page<Member> memberPage;
    private Page<MemberDTOWithoutPw> memberDtoPage;
    private Pageable pageable;
    private MemberDTOWithPasswordConfirm memberDTOWithPasswordConfirm;
    private Role role;
    private String verificationToken;

    @BeforeEach
    public void setUp() {
        memberDTOWithoutId = new MemberDTOWithoutId();
        memberDTOWithoutId.setEmail("test@example.com");
        memberDTOWithoutId.setPassword("Password123!");
        memberDTOWithoutId.setPasswordConfirm("Password123!");
        memberDTOWithoutId.setName("Test User");
        memberDTOWithoutId.setPhone("010-1234-5678");

        member = new Member();
        member.setId(1L);
        member.setEmail(memberDTOWithoutId.getEmail());
        member.setPassword(memberDTOWithoutId.getPassword());
        member.setName(memberDTOWithoutId.getName());
        member.setPhone(memberDTOWithoutId.getPhone());

        Member member2 = new Member();
        member2.setId(2L);
        member2.setEmail("test2@example.com");
        member2.setPassword("Password456!");
        member2.setName("Test User 2");
        member2.setPhone("010-9876-5432");
        member2.setRole(role);

        role = new Role();
        role.setId(1L);
        role.setRole("user");
        member.setRole(role);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setRole("user");
        member2.setRole(role2);

        memberList = Arrays.asList(member, member2);
        pageable = PageRequest.of(1, 5);
        memberPage = new PageImpl<>(memberList, pageable, memberList.size());
        memberDtoPage = memberPage.map(member -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), member.getRole().getRole()));

        memberDTOWithPasswordConfirm = new MemberDTOWithPasswordConfirm();
        memberDTOWithPasswordConfirm.setEmail("update@example.com");
        memberDTOWithPasswordConfirm.setPassword("UpdatedPass123!");
        memberDTOWithPasswordConfirm.setPasswordConfirm("UpdatedPass123!");
        memberDTOWithPasswordConfirm.setName("Updated User");
        memberDTOWithPasswordConfirm.setPhone("010-1111-2222");

        updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setName(memberDTOWithPasswordConfirm.getName());
        updatedMember.setEmail(memberDTOWithPasswordConfirm.getEmail());
        updatedMember.setPhone(memberDTOWithPasswordConfirm.getPhone());
        updatedMember.setPassword(memberDTOWithPasswordConfirm.getPassword());
        updatedMember.setRole(role);

        verificationToken = "validToken";
    }

    @Nested
    class createMemberTests {
        @Test
        public void createMember_ValidInput_ReturnsTokens() throws Exception {

            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(memberService.findByEmail(memberDTOWithoutId.getEmail())).thenReturn(Optional.of(member));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(role));

            when(memberService.convertToEntity(any(MemberDTOWithoutId.class))).thenReturn(member);


            doNothing().when(memberService).join(any(Member.class));
            doNothing().when(roleService).join(any(Role.class));
            when(jwtTokenProvider.generateToken(any())).thenReturn(new TokenDto("bearer", "accessToken","refreshToken", "role"));


            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithoutId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.accessToken").exists())
                    .andExpect(jsonPath("$.result.role").exists())
                    .andExpect(jsonPath("$.result.successMessage").value("회원가입이 성공적으로 완료되었습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("회원가입 성공"))
                    .andExpect(jsonPath("$.status").value(200));


        }

        @Test
        public void createMember_EmptyInput_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("name", "이름을 입력해주세요.");
            errors.put("email", "이메일을 입력해주세요.");
            errors.put("password", "비밀번호를 입력해주세요.");
            errors.put("passwordConfirm", "비밀번호를 한번 더 입력해주세요.");
            errors.put("phone", "전화번호를 입력해주세요.");

            CustomOptionalResponseBody<Map<String, String>> errorBody = new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value());
            CustomOptionalResponseEntity<Map<String, String>> errorResponse = new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);

            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(errorResponse);

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"name\": \"\", \"email\": \"\", \"password\": \"\", \"passwordConfirm\": \"\", \"phone\": \"\" }"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.name").value("이름을 입력해주세요."))
                    .andExpect(jsonPath("$.result.email").value("이메일을 입력해주세요."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호를 입력해주세요."))
                    .andExpect(jsonPath("$.result.passwordConfirm").value("비밀번호를 한번 더 입력해주세요."))
                    .andExpect(jsonPath("$.result.phone").value("전화번호를 입력해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }



        @Test
        public void createMember_InvalidInput_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "이메일 형식이 올바르지 않습니다.");
            errors.put("password", "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다.");
            errors.put("passwordConfirm", "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다.");
            errors.put("phone", "전화번호 형식이 올바르지 않습니다.");

            CustomOptionalResponseBody<Map<String, String>> errorBody = new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value());
            CustomOptionalResponseEntity<Map<String, String>> errorResponse = new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);

            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(errorResponse);

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithoutId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.email").value("이메일 형식이 올바르지 않습니다."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다."))
                    .andExpect(jsonPath("$.result.passwordConfirm").value("비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다."))
                    .andExpect(jsonPath("$.result.phone").value("전화번호 형식이 올바르지 않습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }
        @Test
        public void createMember_InvalidPasswordConfirm_ReturnsBadRequest() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            memberDTOWithoutId.setPasswordConfirm("WrongPassword123!");

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithoutId)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.errorMessage").value("비밀번호와 비밀번호 확인이 일치하지 않습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }
        @Test
//        @WithMockUser(roles = "USER")
        void createMember_DuplicateEmail_ReturnsConflict() throws Exception {
            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
            when(memberService.convertToEntity(any())).thenThrow(new DataIntegrityViolationException("이미 사용 중인 이메일입니다."));

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithoutId)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorMessage").value("이미 사용 중인 이메일입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        public void createMember_DuplicatePhone_ReturnsConflict() throws Exception {
            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
            when(memberService.convertToEntity(any())).thenThrow(new DataIntegrityViolationException("이미 사용 중인 전화번호입니다."));

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithoutId)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorMessage").value("이미 사용 중인 전화번호입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        public void createMember_DuplicateEmailAndPhone_ReturnsConflict() throws Exception {
            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
            when(memberService.convertToEntity(any())).thenThrow(new DataIntegrityViolationException("이미 사용 중인 이메일과 전화번호입니다."));

            mockMvc.perform(post("/api/member/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithoutId)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorMessage").value("이미 사용 중인 이메일과 전화번호입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409));
        }

    }

    @Nested
    @WithMockUser(roles = {"ADMIN", "CENTER"})
    class findMemberByIdTests {
        @Test
        public void findMemberById_ExistingMember_ReturnsMemberInfo() throws Exception {
            when(memberService.findById(member.getId())).thenReturn(Optional.of(member));
            when(roleService.findById(member.getId())).thenReturn(Optional.of(member.getRole()));


            mockMvc.perform(get("/api/member/member")
                            .param("memberId", String.valueOf(member.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(member.getId()))
                    .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.result.name").value(member.getName()))
                    .andExpect(jsonPath("$.result.phone").value(member.getPhone()))
                    .andExpect(jsonPath("$.result.role").value(member.getRole().getRole()))
                    .andExpect(jsonPath("$.responseMessage").value(member.getId() + "(으)로 가입된 회원정보입니다"))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void findMemberById_NonExistingMember_ReturnsNotFound() throws Exception {
            long nonExistingMemberId = 99L;

            when(memberService.findById(nonExistingMemberId)).thenThrow(new IdNotFoundException(nonExistingMemberId + "(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(get("/api/member/member")
                            .param("memberId", String.valueOf(nonExistingMemberId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorMessage").value(nonExistingMemberId + "(으)로 등록된 회원이 없습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404));
        }

    }

    @Nested
    @WithMockUser(roles = {"ADMIN", "CENTER"})
    class findAllMembersTests {
        @Test
        public void findAllMembers_ExistingMembers_ReturnsMembersPage() throws Exception {

            when(memberService.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));


            mockMvc.perform(get("/api/member/members")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.totalCount").value(memberPage.getTotalElements()))
                    .andExpect(jsonPath("$.result.page").value(memberPage.getNumber()))
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(memberList.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(memberDtoPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].email").value(memberDtoPage.getContent().get(0).getEmail()))
                    .andExpect(jsonPath("$.result.content[0].name").value(memberDtoPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].phone").value(memberDtoPage.getContent().get(0).getPhone()))
                    .andExpect(jsonPath("$.result.content[0].role").exists())
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void findAllMembers_EmptyPage_ReturnsEmptyPage() throws Exception {
            when(memberService.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));
            System.out.println(memberPage.getSize());

            mockMvc.perform(get("/api/member/members")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result").exists())
                        .andExpect(jsonPath("$.result.totalCount").value(memberPage.getTotalElements()))
                        .andExpect(jsonPath("$.result.page").value(memberPage.getNumber()))
                        .andExpect(jsonPath("$.result.content").isArray())
                        .andExpect(jsonPath("$.result.content.length()").value(memberList.size()))
                        .andExpect(jsonPath("$.responseMessage").value("전체 회원입니다."))
                        .andExpect(jsonPath("$.status").value(200));

        }

        @Test
        public void findAllMembers_NegativePageNumber_ReturnsBadRequest() throws Exception {
            when(memberService.findAll(any(Pageable.class))).thenThrow(new IllegalAccessError("Page index must not be less than zero"));;
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));

            mockMvc.perform(get("/api/member/members")
                            .param("name", member.getName())
                            .param("email", member.getEmail())
                            .param("phone", member.getPhone())
                            .param("role", member.getRole().getRole())
                            .param("content", "search content")
                            .param("page", "-1")
                            .param("size", "5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.errorMessage").value("Page index must not be less than zero"))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(400));
        }

    }

    @Nested
    @WithMockUser(roles = {"ADMIN", "CENTER"})
    class searchMembersTests {
        @Test
        public void searchMembers_ExistingMembers_ReturnsMatchingMembersPage() throws Exception {
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
                    .andExpect(jsonPath("$.result.totalCount").value(memberPage.getTotalElements()))
                    .andExpect(jsonPath("$.result.page").value(memberPage.getNumber()))
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(memberList.size()))
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
                    .andExpect(jsonPath("$.result.totalCount").value(memberPage.getTotalElements()))
                    .andExpect(jsonPath("$.result.page").value(memberPage.getNumber()))
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(memberList.size()))
                    .andExpect(jsonPath("$.result.content[0].id").value(memberPage.getContent().get(0).getId()))
                    .andExpect(jsonPath("$.result.content[0].email").value(memberPage.getContent().get(0).getEmail()))
                    .andExpect(jsonPath("$.result.content[0].name").value(memberPage.getContent().get(0).getName()))
                    .andExpect(jsonPath("$.result.content[0].phone").value(memberPage.getContent().get(0).getPhone()))
                    .andExpect(jsonPath("$.result.content[0].role").value(memberPage.getContent().get(0).getRole().getRole()))
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원입니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void searchMembers_EmptyPage_ReturnsEmptyPage() throws Exception {
            when(memberService.findMembers(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));

            mockMvc.perform(get("/api/member/search")
                            .param("page", "1000")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.totalCount").value(0))
                    .andExpect(jsonPath("$.result.page").value(0))
                    .andExpect(jsonPath("$.result.content").isArray())
                    .andExpect(jsonPath("$.result.content.length()").value(0))
                    .andExpect(jsonPath("$.responseMessage").value("전체 회원입니다."))
                    .andExpect(jsonPath("$.status").value(200));

        }
        @Test
        public void searchMembers_NoResults_ReturnsNotFound() throws Exception {
            when(memberService.findMembers(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenThrow(new IdNotFoundException("해당하는 회원이 없습니다."));

            mockMvc.perform(get("/api/member/search")
                            .param("name", "NonExistingName")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorMessage").value("해당하는 회원이 없습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404));
        }



        @Test
        public void searchMembers_NegativePageNumber_ReturnsBadRequest() throws Exception {
            when(memberService.findMembers(any(), any(), any(), any(), any(), any(Pageable.class))).thenThrow(new IllegalAccessError("Page index must not be less than zero"));;
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));

            mockMvc.perform(get("/api/member/search")
                            .param("name", member.getName())
                            .param("email", member.getEmail())
                            .param("phone", member.getPhone())
                            .param("role", member.getRole().getRole())
                            .param("content", "search content")
                            .param("page", "-1")
                            .param("size", "5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.errorMessage").value("Page index must not be less than zero"))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(400));
        }

    }

    */
/*@Nested
    @WithMockUser(roles = {"USER", "ADMIN", "CENTER"})
    class verifyUserTests {
        @Test
        public void verifyUser_ValidInput_ReturnsVerificationToken() throws Exception {
            when(validationCheck.validationChecks(any())).thenReturn(null);
            when(memberService.findById(anyLong())).thenReturn(Optional.of(member));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtTokenProvider.generateVerificationToken(anyString(), anyString())).thenReturn(verificationToken);

            mockMvc.perform(post("/api/member/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new UserDetailsDTO("test@example.com", "password")))
                            .header("id", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.successMessage").value("본인 확인이 완료되었습니다."))
                    .andExpect(jsonPath("$.result.verificationToken").exists())
                    .andExpect(jsonPath("$.responseMessage").value("본인 확인 성공"))
                    .andExpect(jsonPath("$.status").value(200));
        }
*//*

        @Test
        public void verifyUser_EmptyInput_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "이메일을 입력해주세요.");
            errors.put("password", "비밀번호를 입력해주세요.");
            when(validationCheck.validationChecks(any())).thenReturn(new CustomOptionalResponseEntity<>(
                    new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            ));

            mockMvc.perform(post("/api/member/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new UserDetailsDTO(null, null)))
                            .header("id", "1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.email").value("이메일을 입력해주세요."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호를 입력해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        public void verifyUser_InvalidInput_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "이메일 형식이 올바르지 않습니다.");
            errors.put("password", "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다.");
            when(validationCheck.validationChecks(any())).thenReturn(new CustomOptionalResponseEntity<>(
                    new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            ));

            mockMvc.perform(post("/api/member/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new UserDetailsDTO("invalid-email", "weakPassword")))
                            .header("id", "1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.email").value("이메일 형식이 올바르지 않습니다."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        public void verifyUser_NonExistingMember_ReturnsNotFound() throws Exception {
            when(validationCheck.validationChecks(any())).thenReturn(null);
            when(memberService.findById(anyLong())).thenThrow(new IdNotFoundException("0(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(post("/api/member/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new UserDetailsDTO("test@example.com", "Password123!")))
                            .header("id", "0"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorMessage").value("0(으)로 등록된 회원이 없습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        public void verifyUser_EmailMismatch_ReturnsBadRequest() throws Exception {
            when(validationCheck.validationChecks(any())).thenReturn(null);
            when(memberService.findById(anyLong())).thenReturn(Optional.of(member));

            mockMvc.perform(post("/api/member/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new UserDetailsDTO("wrong@example.com", "Password123!")))
                            .header("id", "1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.errorMessage").value("이메일이 일치하지 않습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        public void verifyUser_PasswordMismatch_ReturnsUnauthorized() throws Exception {
            when(validationCheck.validationChecks(any())).thenReturn(null);
            when(memberService.findById(anyLong())).thenReturn(Optional.of(member));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            mockMvc.perform(post("/api/member/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new UserDetailsDTO("test@example.com", "WrongPassword123!")))
                            .header("id", "1"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("비밀번호가 일치하지 않습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("비밀번호가 틀립니다."))
                    .andExpect(jsonPath("$.status").value(401));
        }
    }

    @Nested
    @WithMockUser(roles = {"USER", "ADMIN", "CENTER"})
    class updateMemberTests {
        @Test
        public void updateMember_ExistingMember_ReturnsUpdatedMemberInfo() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(updatedMember.getEmail());
            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn(String.valueOf(updatedMember.getId()));
            when(memberService.update(any(Member.class))).thenReturn(Optional.of(updatedMember));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));


            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithPasswordConfirm))
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(updatedMember.getId()))
                    .andExpect(jsonPath("$.result.email").value(updatedMember.getEmail()))
                    .andExpect(jsonPath("$.result.name").value(updatedMember.getName()))
                    .andExpect(jsonPath("$.result.phone").value(updatedMember.getPhone()))
                    .andExpect(jsonPath("$.result.role").value(updatedMember.getRole().getRole()))
                    .andExpect(jsonPath("$.responseMessage").value("회원 정보가 수정되었습니다."))
                    .andExpect(jsonPath("$.status").value(200));
        }
        @Test
        public void updateMember_EmptyInput_ReturnsBadRequest() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(updatedMember.getEmail());
            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn(String.valueOf(updatedMember.getId()));
            when(memberService.update(any(Member.class))).thenReturn(Optional.of(updatedMember));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));

            Map<String, String> errors = new HashMap<>();
            errors.put("name", "이름을 입력해주세요.");
            errors.put("email", "이메일을 입력해주세요.");
            errors.put("password", "비밀번호를 입력해주세요.");
            errors.put("passwordConfirm", "비밀번호를 한번 더 입력해주세요.");
            errors.put("phone", "전화번호를 입력해주세요.");

            CustomOptionalResponseBody<Map<String, String>> errorBody = new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value());
            CustomOptionalResponseEntity<Map<String, String>> errorResponse = new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);

            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(errorResponse);

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"name\": \"\", \"email\": \"\", \"password\": \"\", \"passwordConfirm\": \"\", \"phone\": \"\"}")
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.name").value("이름을 입력해주세요."))
                    .andExpect(jsonPath("$.result.email").value("이메일을 입력해주세요."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호를 입력해주세요."))
                    .andExpect(jsonPath("$.result.passwordConfirm").value("비밀번호를 한번 더 입력해주세요."))
                    .andExpect(jsonPath("$.result.phone").value("전화번호를 입력해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }




        @Test
        public void updateMember_InvalidInput_ReturnsBadRequest() throws Exception {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "이메일 형식이 올바르지 않습니다.");
            errors.put("password", "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다.");
            errors.put("passwordConfirm", "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다.");
            errors.put("phone", "전화번호 형식이 올바르지 않습니다.");

            CustomOptionalResponseBody<Map<String, String>> errorBody = new CustomOptionalResponseBody<>(Optional.of(errors), "입력값을 확인해주세요.", HttpStatus.BAD_REQUEST.value());
            CustomOptionalResponseEntity<Map<String, String>> errorResponse = new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);

            when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(errorResponse);

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\": \"invalid-email\", \"password\": \"weakPassword\", \"passwordConfirm\": \"weakPassword\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }")
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.email").value("이메일 형식이 올바르지 않습니다."))
                    .andExpect(jsonPath("$.result.password").value("비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다."))
                    .andExpect(jsonPath("$.result.passwordConfirm").value("비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다."))
                    .andExpect(jsonPath("$.result.phone").value("전화번호 형식이 올바르지 않습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        public void updateMember_PasswordMismatch_ReturnsBadRequest() throws Exception {

            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(updatedMember.getEmail());


            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"email\": \"test@example.com\", \"password\": \"1111\", \"passwordConfirm\": \"wrong\", \"name\": \"홍길동\", \"phone\": \"010-1111-1111\" }")
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result.errorMessage").value("비밀번호와 비밀번호 확인이 일치하지 않습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("입력값을 확인해주세요."))
                    .andExpect(jsonPath("$.status").value(400));;
        }
        @Test
        public void updateMember_InvalidToken_ReturnsBadRequest() throws Exception {
            String invalidToken = "invalidToken";
            when(jwtTokenProvider.getEmailFromToken(invalidToken)).thenReturn(null);

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithPasswordConfirm))
                            .header("Verification_Token", invalidToken))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorMessage").value("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요."))
                    .andExpect(jsonPath("$.responseMessage").value("유효하지 않은 토큰입니다."))
                    .andExpect(jsonPath("$.status").value(401));
        }
        @Test
        public void updateMember_DuplicateEmail_ReturnsConflict() throws Exception {

            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(updatedMember.getEmail());
            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn(String.valueOf(updatedMember.getId()));
            when(memberService.update(any(Member.class))).thenThrow(new DataIntegrityViolationException("이미 사용 중인 이메일입니다."));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithPasswordConfirm))
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorMessage").value("이미 사용 중인 이메일입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        public void updateMember_DuplicatePhone_ReturnsConflict() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(updatedMember.getEmail());
            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn(String.valueOf(updatedMember.getId()));
            when(memberService.update(any(Member.class))).thenThrow(new DataIntegrityViolationException("이미 사용 중인 전화번호입니다."));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithPasswordConfirm))
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorMessage").value("이미 사용 중인 전화번호입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409));
        }
        @Test
        public void updateMember_DuplicateEmailAndPhone_ReturnsConflict() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(updatedMember.getEmail());
            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn(String.valueOf(updatedMember.getId()));
            when(memberService.update(any(Member.class))).thenThrow(new DataIntegrityViolationException("이미 사용 중인 이메일과 전화번호입니다."));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));

            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithPasswordConfirm))
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorMessage").value("이미 사용 중인 이메일과 전화번호입니다."))
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.status").value(409));
        }
        @Test
        public void updateMember_NoChanges_ReturnsConflict() throws Exception {
            when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(updatedMember.getEmail());
            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn(String.valueOf(updatedMember.getId()));
            when(memberService.update(any(Member.class))).thenThrow(new DataIntegrityViolationException("변경된 회원 정보가 없습니다."));
            when(roleService.findById(anyLong())).thenReturn(Optional.of(new Role()));


            mockMvc.perform(put("/api/member/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(memberDTOWithPasswordConfirm))
                            .header("Verification_Token", verificationToken))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.responseMessage").value("데이터 무결성 위반 오류입니다."))
                    .andExpect(jsonPath("$.result.errorMessage").value("변경된 회원 정보가 없습니다."))
                    .andExpect(jsonPath("$.status").value(409));

            verify(memberService).update(any(Member.class));
        }

    }

    @Nested
    @WithMockUser(roles = {"ADMIN", "CENTER"})
    class deleteMemberTests {
        @Test
        public void deleteMember_ExistingMember_ReturnsSuccessMessage() throws Exception {
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
        public void deleteMember_NonExistingMember_ReturnsNotFound() throws Exception {
            long memberId = 99L;

            when(memberService.findById(memberId)).thenThrow(new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다."));

            mockMvc.perform(delete("/api/member/delete")
                            .param("memberId", String.valueOf(memberId)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorMessage").value(memberId + "(으)로 등록된 회원이 없습니다."))
                    .andExpect(jsonPath("$.responseMessage").value("검색내용이 존재하지 않습니다."))
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
*/
