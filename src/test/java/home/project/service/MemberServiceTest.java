package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입테스트 케이스")
    void 회원가입() {
        // given
        Member member = new Member();
        member.setEmail("sss@sss.com");
        member.setPassword("124");
        member.setName("12");
        member.setPhone("2412414");

        // when
        memberService.join(member);

        // then
        Member findMember = memberService.findByEmail(member.getEmail()).get();
        Assertions.assertThat(member).isEqualTo(findMember);
    }

    @Test
    void findByEmail() {

    }

    @Test
    void findAll() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteMember() {
    }
}