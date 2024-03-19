package home.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.Member;
import home.project.domain.UserDetails;
import home.project.repository.MemberRepository;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;//mockMvc를 사용하면 실제 웹 서버를 시작하지 않아도 http 요청 시뮬레이션 가능

    @Autowired
    private ObjectMapper objectMapper;//JSON 데이터를 직렬화 하는데 사용되는 객체

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;//비밀번호를 암호화하고 검증하는데 사용




    @BeforeEach
    public void clearData() {
        memberRepository.deleteAll();
    }

    @DisplayName("엔드포인트에 JSON 형식 요청 보내고 응답상태 코드 확인 테스트")
    @Test
    void login() throws Exception {
        // given
        Member member1 = new Member();
        member1.setEmail("sksdmltkek12@naver.com");
        member1.setName("12");
        member1.setPhone("2412414");
        String rawPassword = "1234";
        String encodedPassword = passwordEncoder.encode(rawPassword);//비밀번호 암호화
        member1.setPassword(encodedPassword);
        memberService.join(member1);

        UserDetails userDetails = new UserDetails();
        userDetails.setEmail(member1.getEmail());
        userDetails.setPassword(encodedPassword);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/loginToken/login")//perform메서드를 사용하여 엔드포인트에 POST요청
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk());//andExpect 메서드를 사용하여 응답 상태코드가 200인지 확인
    }
}