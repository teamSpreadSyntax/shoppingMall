package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class MemberTest {

    @LocalServerPort
    int port;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void clearData() {
        memberRepository.deleteAll();
    }

    @DisplayName("회원 가입 테스트")
    @Test
    void testJoinMember() {
        // given
        Member member = new Member();
        member.setEmail("sss@sss.com");
        member.setPassword("124");
        member.setName("12");
        member.setPhone("2412414");

        // when
        memberService.join(member);

        // then
        Member findMember = memberService.findById(member.getId()).orElse(null);
        Assertions.assertThat(findMember).isNotNull();
        Assertions.assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("중복되는 이메일 회원 가입 테스트")
    @Test
    void testDuplicateEmailJoin() {
        // given
        Member member1 = new Member();
        member1.setEmail("sss@sss.com");
        member1.setPassword("1235");
        member1.setName("Kim");
        member1.setPhone("010-0000-0001");
        memberService.join(member1);

        Member member2 = new Member();
        member2.setEmail("sss@sss.com"); // duplicate email
        member2.setPassword("1234");
        member2.setName("Kang");
        member2.setPhone("010-0001-0002");

        // when, then
        assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member2));
    }

    @DisplayName("회원 정보 업데이트 테스트")
    @Test
    void testUpdateMember() {
        // given
        Member member = new Member();
        member.setEmail("sss@sss.com");
        member.setPassword("1235");
        member.setName("Kim");
        member.setPhone("010-000-0001");
        memberService.join(member);

        // when
        member.setName("KimKyu");
        member.setPhone("010-000-0003");
        memberService.update(member);

        // then
        Member updatedMember = memberService.findById(member.getId()).orElse(null);
        Assertions.assertThat(updatedMember).isNotNull();
        Assertions.assertThat(updatedMember.getName()).isEqualTo("KimKyu");
        Assertions.assertThat(updatedMember.getPhone()).isEqualTo("010-000-0003");
    }

    @DisplayName("회원 삭제 테스트")
    @Test
    void testDeleteMember() {
        // given
        Member member = new Member();
        member.setEmail("sss@sss.com");
        member.setPassword("1235");
        member.setName("Kim");
        member.setPhone("010-0000-0001");
        memberService.join(member);

        // when
        memberService.deleteById(member.getId());

        // then
        Assertions.assertThatThrownBy(() -> memberService.findById(member.getId()).orElseThrow())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("로 등록된 회원이 없습니다.");
    }
}
