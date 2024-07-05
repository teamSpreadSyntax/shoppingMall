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
        Member member = new Member();
        member.setEmail("test@naver.com");
        member.setName("테스트 유저");

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberRepository.save(member);

        assertEquals(savedMember.getEmail(), member.getEmail());
        assertEquals(savedMember.getName(), member.getName());

        verify(memberRepository).save(any(Member.class));
    }
}