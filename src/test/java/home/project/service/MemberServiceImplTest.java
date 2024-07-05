package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private Member member2;
    private Member member3;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPhone("010-1234-5678");
        member.setPassword("password");
        member.setName("강민서");

        member2 = new Member();
        member2.setId(2L);
        member2.setEmail("test2@example.com");
        member2.setPhone("010-8765-4321");
        member2.setPassword("anotherPassword");
        member2.setName("강민석");

        member3 = new Member();
        member3.setId(3L);
        member3.setEmail("test3@example.com");
        member3.setPhone("010-1234-4567");
        member3.setPassword("anotherPassword2");
        member3.setName("김규현");
    }

    @Nested
    class JoinTests {
        @Test
        void shouldThrowExceptionWhenEmailAndPhoneDuplicate() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(true);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member));
            assertEquals("이메일과 휴대폰번호가 모두 중복됩니다.", exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenEmailDuplicate() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(true);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(false);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member));
            assertEquals("이메일이 중복됩니다.", exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenPhoneDuplicate() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(false);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member));
            assertEquals("휴대폰번호가 중복됩니다.", exception.getMessage());
        }

        @Test
        void shouldRegisterMemberSuccessfully() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(false);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(false);
            when(passwordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");

            memberService.join(member);

            verify(passwordEncoder).encode("password");
            verify(memberRepository).save(member);

            assertEquals("encodedPassword", member.getPassword());
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void shouldFindMemberByIdSuccessfully() {
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            Optional<Member> findMember = memberService.findById(1L);

            assertTrue(findMember.isPresent());
            assertEquals(member, findMember.get());
        }

        @Test
        void shouldThrowExceptionWhenMemberNotFoundById() {
            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.findById(1L));
            assertEquals("1로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }
    @Nested
    class FindMembersTests {
        @Test
        void shouldFindAllMembers() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Member> page = new PageImpl<>(Arrays.asList(member, member2));

            when(memberRepository.findAll(pageable)).thenReturn(page);
            Page<Member> resultList = memberService.findAll(pageable);

            assertNotNull(resultList);
            assertEquals(2, resultList.getTotalElements());
            Member firstMember = resultList.getContent().get(0);
            assertEquals(member, firstMember);
            Member secondMember = resultList.getContent().get(1);
            assertEquals(member2, secondMember);
        }

        @Test
        void shouldFindMembersByNameSuccessfully() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Member> page = new PageImpl<>(Arrays.asList(member, member2));

            when(memberRepository.findMembers("강민서", "null", "null", "null", pageable)).thenReturn(page);

            Page<Member> resultList = memberService.findMembers("강민서", "null", "null", "null", pageable);

            assertNotNull(resultList);
            assertEquals(2, resultList.getTotalElements());
            Member firstMember = resultList.getContent().get(0);
            assertEquals("강민서", firstMember.getName());
            Member secondMember = resultList.getContent().get(1);
            assertEquals("강민석", secondMember.getName());
        }
    }
    @Nested
    class UpdateTests {
        @Test
        void shouldUpdateMemberSuccessfully() {
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(memberRepository.existsByEmail("sksdmltkek12@naver.com")).thenReturn(false);
            when(memberRepository.existsByPhone("010-1234-5672")).thenReturn(false);
            when(passwordEncoder.encode("newPassword")).thenReturn("encodeNewPassword");

            Member updateMember = new Member();
            updateMember.setId(member.getId());
            updateMember.setEmail("sksdmltkek12@naver.com");
            updateMember.setPhone("010-1234-5672");
            updateMember.setPassword("newPassword");
            updateMember.setName("강민수");

            Optional<Member> resultMember = memberService.update(updateMember);
            assertTrue(resultMember.isPresent());
            assertEquals("sksdmltkek12@naver.com", resultMember.get().getEmail());
            assertEquals("010-1234-5672", resultMember.get().getPhone());
            assertEquals("encodeNewPassword", resultMember.get().getPassword());
            assertEquals("강민수", resultMember.get().getName());
        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void shouldDeleteMemberByIdSuccessfully() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            memberService.deleteById(member.getId());

            verify(memberRepository).deleteById(member.getId());
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistentMember() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.deleteById(member.getId()));
            assertEquals(member.getId() + "로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }
}
