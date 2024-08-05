package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;
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
    private Member updateMember;

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

        updateMember = new Member();
        updateMember.setId(member.getId());
        updateMember.setEmail("test4@example.com");
        updateMember.setPhone("010-2345-5678");
        updateMember.setPassword("newPassword");
        updateMember.setName("강길동");
    }

    @Nested
    class convertToEntityTests {
        @Test
        void convertToEntity_ValidInput_ReturnsEncodedMember() {
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

            Member member = memberService.convertToEntity(memberDTOWithoutId);

            assertEquals("encodedPassword", member.getPassword());
            verify(passwordEncoder).encode("password");
        }
    }

    @Nested
    class JoinTests {
        @Test
        void join_ValidInput_SavesMember() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(false);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(false);
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            memberService.join(member);

            verify(memberRepository).save(member);
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
            assertEquals("이미 사용 중인 전화번호입니다.", exception.getMessage());
        }
        @Test
        void join_DuplicateEmailAndPhone_ThrowsDataIntegrityViolationException() {
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(true);
            when(memberRepository.existsByPhone(member.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(member));
            assertEquals("이미 사용 중인 이메일과 전화번호입니다.", exception.getMessage());
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void findById_ExistingMember_ReturnsMember() {
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            Optional<Member> findMember = memberService.findById(1L);

            assertTrue(findMember.isPresent());
            assertEquals(member, findMember.get());
        }

        @Test
        void findById_NonExistingMember_ThrowsIllegalArgumentException() {
            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.findById(1L));
            assertEquals("1(으)로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }

    @Nested
    class FindAllTests {
        @Test
        void findAll_AllMembersFound_ReturnsMembersPage() {
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
        void findMembers_ExistingMembers_ReturnsMatchingMembers() {
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
        void findMembers_NoMatchingMembers_ThrowsIllegalArgumentException() {
            Page<Member> emptyPage = Page.empty(pageable);

            when(memberRepository.findMembers("1", "2", "3", null, "null", pageable)).thenReturn(emptyPage);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.findMembers("1", "2", "3", null, "null", pageable));
            assertEquals("해당하는 회원이 없습니다.", exception.getMessage());
        }

    }

    @Nested
    class UpdateTests {
        @Test
        void update_ExistingMember_ReturnsUpdatedMember() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<Member> result = memberService.update(updateMember);
            assertTrue(result.isPresent());
            Member updatedMember = result.get();
            assertEquals(updateMember.getEmail(), updatedMember.getEmail());
            assertEquals(updateMember.getPhone(), updatedMember.getPhone());
            assertEquals("encodedNewPassword", updatedMember.getPassword());
            assertEquals(updateMember.getName(), updatedMember.getName());
        }

        @Test
        void update_PartialChange_ReturnsUpdatedMember() {
            Member partialUpdateMember = new Member();
            partialUpdateMember.setId(member.getId());
            partialUpdateMember.setName(updateMember.getName());

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Optional<Member> result = memberService.update(partialUpdateMember);

            assertTrue(result.isPresent());
            Member updatedMember = result.get();
            assertEquals(updateMember.getName(), updatedMember.getName());
            assertEquals(member.getEmail(), updatedMember.getEmail());
            assertEquals(member.getPhone(), updatedMember.getPhone());
            assertEquals(member.getPassword(), updatedMember.getPassword());
        }

        @Test
        void update_NewPassword_ReturnsEncodedPassword() {
            Member passwordUpdateMember = new Member();
            passwordUpdateMember.setId(member.getId());
            passwordUpdateMember.setPassword("newPassword");

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
            Optional<Member> result = memberService.update(passwordUpdateMember);

            assertTrue(result.isPresent());
            Member updatedMember = result.get();
            assertEquals("encodedNewPassword", updatedMember.getPassword());
            verify(passwordEncoder).encode("newPassword");
        }

        @Test
        void update_DuplicateEmail_ThrowsDataIntegrityViolationException() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(memberRepository.existsByEmail(updateMember.getEmail())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
            assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
        }

        @Test
        void update_DuplicatePhone_ThrowsDataIntegrityViolationException() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(memberRepository.existsByPhone(updateMember.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
            assertEquals("이미 사용 중인 전화번호입니다.", exception.getMessage());
        }

        @Test
        void update_DuplicateEmailAndPhone_ThrowsDataIntegrityViolationException(){
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(memberRepository.existsByEmail(updateMember.getEmail())).thenReturn(true);
            when(memberRepository.existsByPhone(updateMember.getPhone())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
            assertEquals("이미 사용 중인 이메일과 전화번호입니다.", exception.getMessage());
        }

        @Test
        void update_NonExistingMember_ThrowsIllegalArgumentException() {
            Member nonExistingUpdateMember = new Member();
            nonExistingUpdateMember.setId(999L);

            when(memberRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.update(nonExistingUpdateMember));
            assertEquals("999(으)로 등록된 회원이 없습니다.", exception.getMessage());
        }

        @Test
        void update_NoChanges_ThrowsDataIntegrityViolationException(){
            Member noChangeMember = new Member();
            noChangeMember.setId(member.getId());

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(passwordEncoder.matches("samePassword", member.getPassword())).thenReturn(true);

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(noChangeMember));
            assertEquals("변경된 회원 정보가 없습니다.", exception.getMessage());

        }


    }

    @Nested
    class DeleteByIdTests {
        @Test
        void deleteById_ExistingMember_DeletesSuccessfully() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

            memberService.deleteById(member.getId());

            verify(memberRepository).deleteById(member.getId());
        }

        @Test
        void deleteById_NonExistingMember_ThrowsIllegalArgumentException() {
            when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memberService.deleteById(member.getId()));
            assertEquals(member.getId() + "(으)로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }
}
