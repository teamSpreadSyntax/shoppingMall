//package home.project.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import home.project.domain.*;
//import home.project.service.JwtTokenProvider;
//import home.project.service.MemberService;
//import home.project.service.ValidationCheck;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.validation.BindingResult;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(MemberController.class)
//public class MemberControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MemberService memberService;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    @MockBean
//    private ValidationCheck validationCheck;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService, jwtTokenProvider, validationCheck)).build();
//    }
//
//    @DisplayName("회원가입 성공 테스트")
//    @Test
//    public void 회원가입_성공() throws Exception {
//        // given
//        MemberDTOWithoutId memberDTO = new MemberDTOWithoutId("sss@sss.com", "password", "Test User", "010-1234-5678");
//        when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
//        when(jwtTokenProvider.generateToken(any())).thenReturn(new TokenDto("accessToken", "refreshToken"));
//
//        // when
//        mockMvc.perform(post("/api/member/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberDTO)))
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.message").value("회원가입이 성공적으로 완료되었습니다."));
//    }
//
//    @DisplayName("회원가입 유효성 검사 실패 테스트")
//    @Test
//    public void 회원가입_유효성_검사_실패() throws Exception {
//        // given
//        MemberDTOWithoutId memberDTO = new MemberDTOWithoutId("", "password", "Test User", "010-1234-5678");
//        when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(new CustomOptionalResponseEntity<>(new CustomOptionalResponseBody<>(Optional.empty(), "유효성 검사 실패", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST));
//
//        // when
//        mockMvc.perform(post("/api/member/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberDTO)))
//                // then
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.body.message").value("유효성 검사 실패"));
//    }
//
//    @DisplayName("ID로 회원 조회 테스트")
//    @Test
//    public void ID로_회원_조회() throws Exception {
//        // given
//        Member member = new Member(1L, "sss@sss.com", "password", "Test User", "010-1234-5678");
//        when(memberService.findById(1L)).thenReturn(Optional.of(member));
//
//        // when
//        mockMvc.perform(get("/api/member/member")
//                        .param("memberId", "1"))
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.data.email").value("sss@sss.com"));
//    }
//
//    @DisplayName("전체 회원 조회 테스트")
//    @Test
//    @WithMockUser
//    public void 전체_회원_조회() throws Exception {
//        // given
//        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
//        Page<Member> memberPage = new PageImpl<>(List.of(new Member(1L, "test1@sss.com", "password1", "Test User1", "010-1234-5678"),
//                new Member(2L, "test2@sss.com", "password2", "Test User2", "010-8765-4321")), pageable, 2);
//        when(memberService.findAll(any(Pageable.class))).thenReturn(memberPage);
//
//        // when
//        mockMvc.perform(get("/api/member/members")
//                        .param("page", "1")
//                        .param("size", "5"))
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.totalCount").value(2));
//    }
//
//    @DisplayName("회원 검색 테스트")
//    @Test
//    public void 회원_검색() throws Exception {
//        // given
//        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());
//        Page<Member> memberPage = new PageImpl<>(List.of(new Member(1L, "test1@sss.com", "password1", "Test User1", "010-1234-5678")), pageable, 1);
//        when(memberService.findMembers(anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(memberPage);
//
//        // when
//        mockMvc.perform(get("/api/member/search")
//                        .param("name", "Test User1")
//                        .param("page", "1")
//                        .param("size", "5"))
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.totalCount").value(1));
//    }
//
//    @DisplayName("회원 정보 업데이트 테스트")
//    @Test
//    public void 회원_정보_업데이트() throws Exception {
//        // given
//        Member member = new Member(1L, "sss@sss.com", "newpassword", "Updated User", "010-1234-5678");
//        when(validationCheck.validationChecks(any(BindingResult.class))).thenReturn(null);
//        when(memberService.update(any(Member.class))).thenReturn(Optional.of(member));
//
//        // when
//        mockMvc.perform(put("/api/member/update")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(member)))
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.message").value("회원 정보가 수정되었습니다"));
//    }
//
//    @DisplayName("회원 삭제 테스트")
//    @Test
//    public void 회원_삭제() throws Exception {
//        // given
//        doNothing().when(memberService).deleteById(1L);
//
//        // when
//        mockMvc.perform(delete("/api/member/delete")
//                        .param("memberId", "1"))
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.message").value("회원 삭제 성공"));
//    }
//}
