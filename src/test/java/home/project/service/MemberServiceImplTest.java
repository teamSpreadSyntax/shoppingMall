package home.project.service;

import home.project.domain.Member;
import home.project.dto.MemberDTOWithoutId;
import home.project.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Member member;
    private Member member2;
    private Member member3;
    private MemberDTOWithoutId memberDTOWithoutId;
    private Pageable pageable;
    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPhone("010-1111-1111");
        member.setPassword("password");
        member.setName("김길동");

        member2 = new Member();
        member2.setId(2L);
        member2.setEmail("test2@example.com");
        member2.setPhone("010-2222-2222");
        member2.setPassword("otherPassword");
        member2.setName("홍길동");

        member3 = new Member();
        member3.setId(3L);
        member3.setEmail("test3@example.com");
        member3.setPhone("010-3333-3333");
        member3.setPassword("anotherPassword");
        member3.setName("박길동");

        memberDTOWithoutId = new MemberDTOWithoutId();
        memberDTOWithoutId.setEmail("test@example.com");
        memberDTOWithoutId.setPassword("password");
        memberDTOWithoutId.setName("Test User");
        memberDTOWithoutId.setPhone("010-1234-5678");

        pageable = PageRequest.of(0, 10);
    }
    @Nested
    class convertToEntityTests {
    @Test
    void convertToEntity_EncodesPassword_returnMember() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        Member member = memberService.convertToEntity(memberDTOWithoutId);

        assertEquals("encodedPassword", member.getPassword());
        verify(passwordEncoder).encode("password");
    }
}

    @Nested
    class JoinTests {
        @Test
        void join_DuplicateEmailAndPhone_ThrowsDataIntegrityViolationException() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(true);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member));
            assertEquals("이미 사용 중인 이메일과 휴대폰번호입니다.", exception.getMessage());
        }

        @Test
        void join_DuplicateEmail_ThrowsDataIntegrityViolationException() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(true);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(false);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member));
            assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
        }

        @Test
        void join_DuplicatePhone_ThrowsDataIntegrityViolationException() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(false);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member));
            assertEquals("이미 사용 중인 휴대폰번호입니다.", exception.getMessage());
        }

        @Test
        void join_SuccessfullyJoinsMember_EncodesPasswordAndSavesMember() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(false);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(false);
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            memberService.join(member);

            verify(memberRepository).save(member);
        }

    }


    @Nested
    class FindByIdTests {
        @Test
        void findById_ExistingId_ReturnMember() {
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            Optional<Member> findMember = memberService.findById(1L);

            assertTrue(findMember.isPresent());
            assertEquals(member, findMember.get());
        }

        @Test
        void findById_NonExistingId_ThrowsException() {
            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.findById(1L));
            assertEquals("1(으)로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class FindAllTests {
        @Test
        void findAll_AllMembersFound_ReturnsPageOfMembers() {
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
    }

    @Nested
    class FindMembersTests {
        @Test
        void findMembers_ByCondition_ReturnsMatchingMembers() {
            Page<Member> page = new PageImpl<>(Arrays.asList(member, member2));

            when(memberRepository.findMembers("김길동", "null", "null", "null", "null", pageable)).thenReturn(page);

            Page<Member> resultList = memberService.findMembers("김길동", "null", "null", "null", "null", pageable);

            assertNotNull(resultList);
            assertEquals(2, resultList.getTotalElements());
            Member firstMember = resultList.getContent().get(0);
            assertEquals("김길동", firstMember.getName());
            Member secondMember = resultList.getContent().get(1);
            assertEquals("홍길동", secondMember.getName());
        }

        @Test
        void findMembers_NoMatchingMembers_ThrowsException() {
            Page<Member> emptyPage = Page.empty(pageable);

            when(memberRepository.findMembers("1", "2", "3", null, "null", pageable)).thenReturn(emptyPage);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.findMembers("1", "2", "3", null, "null", pageable));
            assertEquals("해당하는 회원이 없습니다.", exception.getMessage());
        }

    }

    @Nested
    class UpdateTests {
        @Test
        void update_MemberSuccessfullyUpdated_ReturnsUpdatedMember() {
            Member updateMember = new Member();
            updateMember.setId(member.getId());
            updateMember.setEmail("test@example.com");
            updateMember.setPhone("010-1234-5678");
            updateMember.setPassword("newPassword");
            updateMember.setName("김길동");

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<Member> resultMember = memberService.update(updateMember);
            assertTrue(resultMember.isPresent());
            assertEquals("test@example.com", resultMember.get().getEmail());
            assertEquals("010-1234-5678", resultMember.get().getPhone());
            assertEquals("encodedNewPassword", resultMember.get().getPassword());
            assertEquals("김길동", resultMember.get().getName());
        }

        @Test
        void update_ProductPartialChangeUpdated_ReturnUpdatedMember() {
            Member updateMember = new Member();
            updateMember.setId(member.getId());
            updateMember.setEmail("test2@example.com");
            updateMember.setPhone("010-1234-5678");
            updateMember.setPassword("newPassword");
            updateMember.setName("김길동");

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(memberRepository.existsByEmail(updateMember.getEmail())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
            assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
        }
        @Test
        void update_DuplicatePhone_ThrowsDataIntegrityViolationException() {
                Member updateMember = new Member();
                updateMember.setId(member.getId());
                updateMember.setEmail("test4@example.com");
                updateMember.setPhone("010-2345-5678");
                updateMember.setPassword("newPassword");
                updateMember.setName("김길동");

                when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
                when(memberRepository.existsByPhone(updateMember.getPhone())).thenReturn(true);

                DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
                assertEquals("이미 사용 중인 휴대폰번호입니다.", exception.getMessage());
            }
        @Test
        void update_DuplicateEmail_ThrowsDataIntegrityViolationException(){
            Member updateMember = new Member();
            updateMember.setId(member.getId());
            updateMember.setEmail("test4@example.com");
            updateMember.setPhone("010-2345-5678");
            updateMember.setPassword("newPassword");
            updateMember.setName("김길동");

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(memberRepository.existsByEmail(updateMember.getEmail())).thenReturn(true);
            when(memberRepository.existsByPhone(updateMember.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));

            assertEquals("이미 사용 중인 이메일과 휴대폰번호입니다.", exception.getMessage());
        }
        @Test
        void 업데이트_변경사항_없음(){
            Member updateMember = new Member();
            updateMember.setId(member.getId());
            updateMember.setEmail(member.getEmail());
            updateMember.setPhone(member.getPhone());
            updateMember.setName(member.getName());
            updateMember.setPassword("samePassword");

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("samePassword", member.getPassword())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember)
            );

            assertEquals("변경된 회원 정보가 없습니다.", exception.getMessage());

        }
        @Test
        void 업데이트_일부만_변경() {
            Member updateMember = new Member();
            updateMember.setId(member.getId());
            updateMember.setName("새이름");

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(memberRepository.save(any(Member.class))).thenAnswer(i -> i.getArguments()[0]);

            Optional<Member> result = memberService.update(updateMember);

            assertTrue(result.isPresent());
            assertEquals("새이름", result.get().getName());
            assertEquals(member.getEmail(), result.get().getEmail());
            assertEquals(member.getPhone(), result.get().getPhone());
            assertEquals(member.getPassword(), result.get().getPassword());
        }
        @Test
        void 업데이트_비밀번호_변경_인코딩() {
            Member updateMember = new Member();
            updateMember.setId(member.getId());
            updateMember.setPassword("newPassword");

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
            when(memberRepository.save(any(Member.class))).thenAnswer(i -> i.getArguments()[0]);

            Optional<Member> result = memberService.update(updateMember);

            assertTrue(result.isPresent());
            assertEquals("encodedNewPassword", result.get().getPassword());
            verify(passwordEncoder).encode("newPassword");
        }
        @Test
        void 업데이트_존재하지_않는_회원() {
            Member updateMember = new Member();
            updateMember.setId(999L);
            updateMember.setName("새이름");

            when(memberRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.findById(999L));
            assertEquals("999(으)로 등록된 회원이 없습니다.", exception.getMessage());        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void deleteById_ExistingId_MemberDeletedSuccessfully() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            memberService.deleteById(member.getId());

            verify(memberRepository).deleteById(member.getId());
        }

        @Test
        void deleteById_NonExistentId_ThrowsException() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.deleteById(member.getId()));
            assertEquals(member.getId() + "(으)로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }
}
