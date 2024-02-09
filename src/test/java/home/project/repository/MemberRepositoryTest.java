package home.project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberRepositoryTest {

    @Mock
    private MemberRepository memberRepository;

    @Test
    public void testSaveMember() {
        // 샘플 Member 객체 생성
        Member member = new Member();
        member.setEmail(1);
        member.setName("테스트 유저");
        // 필요한 다른 필드 설정

        // memberRepository의 save 메서드를 목 객체로 만들어서 저장된 member를 반환하도록 설정
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // save 메서드 호출
        Member savedMember = memberRepository.save(member);

        // 저장된 멤버를 반환하는지 확인
        assertEquals(savedMember.getEmail(), member.getEmail());
        assertEquals(savedMember.getName(), member.getName());
        // 다른 필드에 대해서도 필요에 따라 확인할 수 있습니다

        // memberRepository의 save 메서드가 호출되었는지 확인
        verify(memberRepository).save(any(Member.class));
    }
}



//package home.project.repository;
//
//
//import home.project.domain.Member;
//import jakarta.transaction.Transactional;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//class MemberServiceTest {
//
//    MemberRepository memberRepository;
//    Member member;
//    @Test
//    @Transactional
//    void save() {
//        //given
//        Member member = new Member();
//        member.setName("spring");
////        void join(Member member) {
////            return this.member = member;
////        }
////
//        //when
//        memberRepository.save(member);
//        Member findMember = memberRepository.findMemberByName("spring");
//
//        //then
//        Assertions.assertThat(member).isEqualTo(findMember);
//    }
//}
//






//import home.project.domain.Member;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import static org.assertj.core.api.Assertions.assertThat;
//@SpringBootTest
//@Transactional
//class MemberRepositoryTest {
//    @Autowired
//    private MemberRepository repository;
//        @Test
//        public void save() {
//            Member member = new Member();
//            member.setName("spring");
//
//            repository.save(member);
//
//            Member result = repository.findMemberByName(member.getName());
//            assertThat(member).isEqualTo(result);
//        }
//
//
//    }
