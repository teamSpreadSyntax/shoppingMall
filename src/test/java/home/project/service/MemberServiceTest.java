//package home.project.service;
//
//import home.project.domain.Member;
//import home.project.repository.MemberRepository;
//import jakarta.transaction.Transactional;
//import org.assertj.core.api.Assertions;
//import org.assertj.core.api.OptionalAssert;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Transactional
//class MemberServiceTest {
//    @LocalServerPort
//    int port;
//
//
//    @Autowired MemberService memberService;
//    @Autowired MemberRepository memberRepository;
//
//    @BeforeEach
//    public void clearData(){
//        memberRepository.deleteAll();
//    }
//
//    @DisplayName("회원가입테스트 케이스")
//    @Test
//    void 회원가입() {
//        // given
//        Member member = new Member();
//        member.setEmail("sss@sss.com");
//        member.setPassword("124");
//        member.setName("12");
//        member.setPhone("2412414");
//
//        // when
//        memberService.join(member);
//
//        // then
//        Member findMember = memberService.findByEmail(member.getEmail()).get();
//        Assertions.assertThat(member).isEqualTo(findMember);
//    }
//    @DisplayName("중복되는 이메일이 있는지 확인")
//    @Test
//    void findByEmail() {
//        // given
//        Member member = new Member();
//        member.setEmail("sss@sss.com");
//        member.setPassword("124");
//        member.setName("12");
//        member.setPhone("2412414");
//        memberService.join(member);
//
//        //when
//       Member findEmail = memberService.findByEmail(member.getEmail()).get();
//
//        //then
//        Assertions.assertThat(member.getEmail()).isEqualTo(findEmail.getEmail());
//    }
//    @DisplayName("2개의 계정이 데이터베이스에 있는지 확인")
//    @Test
//    void findAll() {
//        // given
//        Member member1 = new Member();
//        member1.setEmail("sss@sss.com");
//        member1.setPassword("1235");
//        member1.setName("Kim");
//        member1.setPhone("010-0000-0001");
//        memberService.join(member1);
//        Member member2 = new Member();
//        member2.setEmail("ss@ss.com");
//        member2.setPassword("1234");
//        member2.setName("Kang");
//        member2.setPhone("010-0001-0002");
//        memberService.join(member2);
//        Pageable pageable = PageRequest.of(0, 10); // 페이지 번호와 페이지 크기를 지정합니다.
//        Page<Member> memberPage = memberService.findAll(pageable);
//        //then
//        Assertions.assertThat(memberPage.getTotalElements()).isEqualTo(2);
//
//    }
//    @DisplayName("이름을 변경하여 이메일로 같은지 확인")
//    @Test
//    void update() {
//        //given
//        Member member1 = new Member();
//        member1.setEmail("sss@sss.com");
//        member1.setPassword("1235");
//        member1.setName("Kim");
//        member1.setPhone("010-0000-0001");
//        memberService.join(member1);
//        //when
//        member1.setName("KimKyu");
//        memberService.update(member1);
//        //then
//        Member updateMember = memberService.findByEmail(member1.getEmail()).get();
//        Assertions.assertThat(updateMember.getEmail()).isEqualTo(member1.getEmail());
//
//    }
//
//    @DisplayName("이메일을 확인하여 계정 삭제")
//    @Test
//    void deleteMember() {
//        // Given (데이터 준비)
//        Member member1 = new Member();
//        member1.setEmail("sss@sss.com");
//        member1.setPassword("1235");
//        member1.setName("Kim");
//        member1.setPhone("010-0000-0001");
//
//        memberService.join(member1);
//
//// When (실행)
//        memberService.deleteMember(member1.getId());
//
//// Then (결과 검증)
//        try {
//            assertThrows(IllegalArgumentException.class, () -> {
//                memberService.findByEmail
//                        (member1.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//            });
//        }catch (IllegalArgumentException e){
//            System.out.print("삭제성공");
//        }
//    }
//}