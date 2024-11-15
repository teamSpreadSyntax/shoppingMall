package home.project.service.memberService;

import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.repository.member.MemberRepository;
import home.project.service.member.MemberService;
import home.project.service.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class test {
    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private Member member;
    private Member member2;
    private Member member3;
    private CreateMemberRequestDTO createMemberRequestDTO;
    private Pageable pageable;
    private UpdateMemberRequestDTO updateMemberRequestDTO;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPhone("010-1111-1111");
        member.setPassword("password");
        member.setName("김길동");
        member.setRole(RoleType.user);

        member2 = new Member();
        member2.setId(2L);
        member2.setEmail("test2@example.com");
        member2.setPhone("010-2222-2222");
        member2.setPassword("otherPassword");
        member2.setName("홍길동");
        member2.setRole(RoleType.user);

        member3 = new Member();
        member3.setId(3L);
        member3.setEmail("test3@example.com");
        member3.setPhone("010-3333-3333");
        member3.setPassword("anotherPassword");
        member3.setName("박길동");
        member3.setRole(RoleType.user);

        createMemberRequestDTO = new CreateMemberRequestDTO();
        createMemberRequestDTO.setEmail("test@example.com");
        createMemberRequestDTO.setPassword("password");
        createMemberRequestDTO.setPasswordConfirm("password");
        createMemberRequestDTO.setName("Test User");
        createMemberRequestDTO.setPhone("010-1234-5678");

        pageable = PageRequest.of(0, 5);

        updateMemberRequestDTO = new UpdateMemberRequestDTO();
        updateMemberRequestDTO.setEmail("test4@example.com");
        updateMemberRequestDTO.setPhone("010-2345-5678");
        updateMemberRequestDTO.setPassword("newPassword");
        updateMemberRequestDTO.setPasswordConfirm("newPassword");
        updateMemberRequestDTO.setName("강길동");
    }
}
