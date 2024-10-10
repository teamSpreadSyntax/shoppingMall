//package home.project.service;
//
//import home.project.domain.Member;
//import home.project.domain.RoleType;
//import home.project.dto.requestDTO.CreateMemberRequestDTO;
//import home.project.dto.requestDTO.UpdateMemberRequestDTO;
//import home.project.dto.requestDTO.VerifyUserRequestDTO;
//import home.project.dto.responseDTO.MemberResponse;
//import home.project.dto.responseDTO.TokenResponse;
//import home.project.exceptions.exception.IdNotFoundException;
//import home.project.exceptions.exception.NoChangeException;
//import home.project.repository.MemberRepository;
//import io.jsonwebtoken.JwtException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//class MemberServiceImplTest {
//
//    @Autowired
//    private MemberService memberService;
//
//    @MockBean
//    private MemberRepository memberRepository;
//
//    @MockBean
//    private PasswordEncoder passwordEncoder;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    private Member member;
//    private Member member2;
//    private Member member3;
//    private CreateMemberRequestDTO createMemberRequestDTO;
//    private Pageable pageable;
//    private UpdateMemberRequestDTO updateMemberRequestDTO;
//
//    @BeforeEach
//    void setUp() {
//        member = new Member();
//        member.setId(1L);
//        member.setEmail("test@example.com");
//        member.setPhone("010-1111-1111");
//        member.setPassword("password");
//        member.setName("김길동");
//        member.setRole(RoleType.user);
//
//        member2 = new Member();
//        member2.setId(2L);
//        member2.setEmail("test2@example.com");
//        member2.setPhone("010-2222-2222");
//        member2.setPassword("otherPassword");
//        member2.setName("홍길동");
//        member2.setRole(RoleType.user);
//
//        member3 = new Member();
//        member3.setId(3L);
//        member3.setEmail("test3@example.com");
//        member3.setPhone("010-3333-3333");
//        member3.setPassword("anotherPassword");
//        member3.setName("박길동");
//        member3.setRole(RoleType.user);
//
//        createMemberRequestDTO = new CreateMemberRequestDTO();
//        createMemberRequestDTO.setEmail("test@example.com");
//        createMemberRequestDTO.setPassword("password");
//        createMemberRequestDTO.setPasswordConfirm("password");
//        createMemberRequestDTO.setName("Test User");
//        createMemberRequestDTO.setPhone("010-1234-5678");
//
//        pageable = PageRequest.of(0, 5);
//
//        updateMemberRequestDTO = new UpdateMemberRequestDTO();
//        updateMemberRequestDTO.setEmail("test4@example.com");
//        updateMemberRequestDTO.setPhone("010-2345-5678");
//        updateMemberRequestDTO.setPassword("newPassword");
//        updateMemberRequestDTO.setPasswordConfirm("newPassword");
//        updateMemberRequestDTO.setName("강길동");
//    }
//
//    @Nested
//    class JoinTests {
//        @Test
//        void join_ValidInput_SavesMemberAndReturnsToken() {
//            when(memberRepository.existsByEmail(anyString())).thenReturn(false);
//            when(memberRepository.existsByPhone(anyString())).thenReturn(false);
//            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//            when(memberRepository.save(any(Member.class))).thenReturn(member);
//            when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
//            when(jwtTokenProvider.generateToken(any())).thenReturn(new TokenResponse("Bearer", "token", "refreshToken", null));
//            when(memberRepository.findById(any())).thenReturn(Optional.of(member));
//
//            TokenResponse result = memberService.join(createMemberRequestDTO);
//
//            assertNotNull(result);
//            assertEquals(RoleType.user, result.getRole());
//            assertEquals("Bearer", result.getGrantType());
//            verify(memberRepository).save(any(Member.class));
//            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//            verify(jwtTokenProvider).generateToken(any(Authentication.class));
//        }
//        @Test
//        void join_PasswordMismatch_ThrowsIllegalStateException() {
//            createMemberRequestDTO.setPasswordConfirm("differentPassword");
//
//            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.join(createMemberRequestDTO));
//            assertEquals("비밀번호와 비밀번호 확인이 일치하지 않습니다.", exception.getMessage());
//
//        }
//        @Test
//        void join_DuplicateEmail_ThrowsDataIntegrityViolationException() {
//            when(memberRepository.existsByEmail(anyString())).thenReturn(true);
//
//            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(createMemberRequestDTO));
//            assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
//        }
//        @Test
//        void join_DuplicatePhone_ThrowsDataIntegrityViolationException() {
//            when(memberRepository.existsByEmail(anyString())).thenReturn(false);
//            when(memberRepository.existsByPhone(anyString())).thenReturn(true);
//
//            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(createMemberRequestDTO));
//            assertEquals("이미 사용 중인 전화번호입니다.", exception.getMessage());
//        }
//        @Test
//        void join_DuplicateEmailAndPhone_ThrowsDataIntegrityViolationException() {
//            when(memberRepository.existsByPhone(anyString())).thenReturn(true);
//            when(memberRepository.existsByEmail(anyString())).thenReturn(true);
//
//
//            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.join(createMemberRequestDTO));
//            assertEquals("이미 사용 중인 이메일과 전화번호입니다.", exception.getMessage());
//        }
//    }
//    @Nested
//    class MemberInfoTests {
//        @Test
//        void memberInfo_AuthenticatedUser_ReturnsMemberResponse() {
//            // 기존 코드
//            Authentication authentication = mock(Authentication.class);
//            when(authentication.getName()).thenReturn("test@example.com");
//
//            when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
//
//            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//
//            try (var mocked = mockStatic(SecurityContextHolder.class)) {
//                mocked.when(SecurityContextHolder::getContext).thenReturn(mock(SecurityContext.class));
//                when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
//
//                MemberResponse result = memberService.memberInfo();
//
//                assertNotNull(result);
//                assertEquals(member.getId(), result.getId());
//                assertEquals(member.getEmail(), result.getEmail());
//                assertEquals(member.getName(), result.getName());
//                assertEquals(member.getPhone(), result.getPhone());
//                assertEquals(member.getRole(), result.getRole());
//            }
//        }
//    }
//    @Nested
//    class FindByIdTests {
//        @Test
//        void findById_ExistingMember_ReturnsMember() {
//            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//            Member findMember = memberService.findById(1L);
//
//            assertNotNull(findMember);
//            assertEquals(member, findMember);
//        }
//
//        @Test
//        void findById_NonExistingMember_ThrowsIdNotFoundException() {
//            when(memberRepository.findById(1L)).thenReturn(Optional.empty());
//
//            assertThrows(IdNotFoundException.class, () -> memberService.findById(1L));
//        }
//    }
//
//    @Nested
//    class FindByEmailTests {
//        @Test
//        void findByEmail_ExistingMember_ReturnsMember() {
//            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
//            Member findMember = memberService.findByEmail(member.getEmail());
//
//            assertNotNull(findMember);
//            assertEquals(member, findMember);
//        }
//
//        @Test
//        void findByEmail_NonExistingMember_ThrowsIdNotFoundException() {
//            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());
//
//            assertThrows(IdNotFoundException.class, () -> memberService.findByEmail(member.getEmail()));
//        }
//    }
//    @Nested
//    class FindAllTests {
//        @Test
//        void findAll_AllMembersFound_ReturnsMembersPage() {
//            Page<Member> page = new PageImpl<>(Arrays.asList(member, member2));
//
//            when(memberRepository.findAll(pageable)).thenReturn(page);
//            Page<Member> resultList = memberService.findAll(pageable);
//
//            assertNotNull(resultList);
//            assertEquals(2, resultList.getTotalElements());
//            assertEquals(member, resultList.getContent().get(0));
//            assertEquals(member2, resultList.getContent().get(1));
//        }
//    }
//    @Nested
//    class FindMembersTests {
//        @Test
//        void findMembers_ExistingMembers_ReturnsMatchingMembers() {
//            Page<Member> page = new PageImpl<>(Arrays.asList(member, member2));
//
//            when(memberRepository.findMembers("김길동", "null", "null", "null", "null", pageable)).thenReturn(page);
//
//            Page<MemberResponse> resultList = memberService.findMembers("김길동", "null", "null", "null", "null", pageable);
//
//            assertNotNull(resultList);
//            assertEquals(2, resultList.getTotalElements());
//            assertEquals("김길동", resultList.getContent().get(0).getName());
//            assertEquals("홍길동", resultList.getContent().get(1).getName());
//        }
//
//        @Test
//        void findMembers_NoMatchingMembers_ThrowsIdNotFoundException() {
//            Page<Member> emptyPage = Page.empty(pageable);
//
//            when(memberRepository.findMembers("1", "2", "3", null, "null", pageable)).thenReturn(emptyPage);
//
//            Page<MemberResponse> result = memberService.findMembers("1", "2", "3", null, "null", pageable);
//
//            assertNotNull(result);
//            assertTrue(result.isEmpty());
//            assertEquals(0, result.getTotalElements());
//            assertEquals(0, result.getContent().size());        }
//    }
//    @Nested
//    class VerifyUserTests {
//        /*@Test
//        void verifyUser_ValidCredentials_ReturnsVerificationToken() {
//            when(memberRepository.findByEmail(member.getEmail()).get().getId()).thenReturn(Optional.of(member));
//            when(passwordEncoder.matches(password, member.getPassword())).thenReturn(true);
//            when(jwtTokenProvider.generateVerificationToken(email, member.getId())).thenReturn("verificationToken");
//
//            // When
//            String result = memberService.verifyUser(email, verifyUserRequestDTO);
//
//            // Then
//            assertNotNull(result);
//            assertEquals("verificationToken", result);
//            verify(memberRepository).findByEmail(email);
//            verify(passwordEncoder).matches(password, member.getPassword());
//            verify(jwtTokenProvider).generateVerificationToken(email, member.getId());
//        }*/
//
//        @Test
//        void verifyUser_InvalidPassword_ThrowsBadCredentialsException() {
//            String email = "test@example.com";
//            VerifyUserRequestDTO password = new VerifyUserRequestDTO();
//            when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
//            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
//
//            assertThrows(BadCredentialsException.class, () -> memberService.verifyUser(email, password));
//        }
//    }
//    @Nested
//    class UpdateTests {
//        @Test
//        void update_ValidInput_ReturnsUpdatedMemberResponse() {
//            String verificationToken = "validToken";
//            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn("test@example.com");
//            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn("1");
//            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//            when(memberRepository.save(any(Member.class))).thenReturn(member);
//
//            MemberResponse result = memberService.update(updateMemberRequestDTO, verificationToken);
//
//            assertNotNull(result);
//            assertEquals(updateMemberRequestDTO.getEmail(), result.getEmail());
//            assertEquals(updateMemberRequestDTO.getName(), result.getName());
//            assertEquals(updateMemberRequestDTO.getPhone(), result.getPhone());
//        }
//
//        @Test
//        void update_InvalidToken_ThrowsJwtException() {
//            String invalidToken = "invalidToken";
//            when(jwtTokenProvider.getEmailFromToken(invalidToken)).thenReturn(null);
//
//            assertThrows(JwtException.class, () -> memberService.update(updateMemberRequestDTO, invalidToken));
//        }
//
//        @Test
//        void update_PasswordMismatch_ThrowsIllegalStateException() {
//            String validToken = "validToken";
//            when(jwtTokenProvider.getEmailFromToken(validToken)).thenReturn("test@example.com");
//            updateMemberRequestDTO.setPasswordConfirm("differentPassword");
//
//            assertThrows(IllegalStateException.class, () -> memberService.update(updateMemberRequestDTO, validToken));
//        }
//
//        @Test
//        void update_NoChanges_ThrowsNoChangeException() {
//            String validToken = "validToken";
//            when(jwtTokenProvider.getEmailFromToken(validToken)).thenReturn("test@example.com");
//            when(jwtTokenProvider.getIdFromVerificationToken(validToken)).thenReturn("1");
//            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//
//            UpdateMemberRequestDTO noChangeDTO = new UpdateMemberRequestDTO();
//            noChangeDTO.setEmail(member.getEmail());
//            noChangeDTO.setName(member.getName());
//            noChangeDTO.setPhone(member.getPhone());
//            noChangeDTO.setPassword(member.getPassword());
//            noChangeDTO.setPasswordConfirm(member.getPassword());
//
//            assertThrows(NullPointerException.class, () -> memberService.update(noChangeDTO, validToken));
//        }
//    }
//
//    @Nested
//    class DeleteByIdTests {
//        @Test
//        void deleteById_ExistingMember_DeletesSuccessfully() {
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//
//            memberService.deleteById(member.getId());
//
//            verify(memberRepository).deleteById(member.getId());
//        }
//
//        @Test
//        void deleteById_NonExistingMember_ThrowsIdNotFoundException() {
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());
//
//            assertThrows(IdNotFoundException.class, () -> memberService.deleteById(member.getId()));
//        }
//    }
//    /*
//
//    @Nested
//    class UpdateTests {
//        @Test
//        void update_ExistingMember_ReturnsUpdatedMember() {
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
//            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            Optional<Member> result = memberService.update(updateMember);
//            assertTrue(result.isPresent());
//            Member updatedMember = result.get();
//            assertEquals(updateMember.getEmail(), updatedMember.getEmail());
//            assertEquals(updateMember.getPhone(), updatedMember.getPhone());
//            assertEquals("encodedNewPassword", updatedMember.getPassword());
//            assertEquals(updateMember.getName(), updatedMember.getName());
//        }
//
//        @Test
//        void update_PartialChange_ReturnsUpdatedMember() {
//            Member partialUpdateMember = new Member();
//            partialUpdateMember.setId(member.getId());
//            partialUpdateMember.setName(updateMember.getName());
//
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            Optional<Member> result = memberService.update(partialUpdateMember);
//
//            assertTrue(result.isPresent());
//            Member updatedMember = result.get();
//            assertEquals(updateMember.getName(), updatedMember.getName());
//            assertEquals(member.getEmail(), updatedMember.getEmail());
//            assertEquals(member.getPhone(), updatedMember.getPhone());
//            assertEquals(member.getPassword(), updatedMember.getPassword());
//        }
//
//        @Test
//        void update_NewPassword_ReturnsEncodedPassword() {
//            Member passwordUpdateMember = new Member();
//            passwordUpdateMember.setId(member.getId());
//            passwordUpdateMember.setPassword("newPassword");
//
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
//            when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
//            Optional<Member> result = memberService.update(passwordUpdateMember);
//
//            assertTrue(result.isPresent());
//            Member updatedMember = result.get();
//            assertEquals("encodedNewPassword", updatedMember.getPassword());
//            verify(passwordEncoder).encode("newPassword");
//        }
//
//        @Test
//        void update_DuplicateEmail_ThrowsDataIntegrityViolationException() {
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//            when(memberRepository.existsByEmail(updateMember.getEmail())).thenReturn(true);
//
//            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
//            assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
//        }
//
//        @Test
//        void update_DuplicatePhone_ThrowsDataIntegrityViolationException() {
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//            when(memberRepository.existsByPhone(updateMember.getPhone())).thenReturn(true);
//
//            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
//            assertEquals("이미 사용 중인 전화번호입니다.", exception.getMessage());
//        }
//
//        @Test
//        void update_DuplicateEmailAndPhone_ThrowsDataIntegrityViolationException(){
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//            when(memberRepository.existsByEmail(updateMember.getEmail())).thenReturn(true);
//            when(memberRepository.existsByPhone(updateMember.getPhone())).thenReturn(true);
//
//            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> memberService.update(updateMember));
//            assertEquals("이미 사용 중인 이메일과 전화번호입니다.", exception.getMessage());
//        }
//
//        @Test
//        void update_NonExistingMember_ThrowsIllegalArgumentException() {
//            Member nonExistingUpdateMember = new Member();
//            nonExistingUpdateMember.setId(999L);
//
//            when(memberRepository.findById(999L)).thenReturn(Optional.empty());
//
//            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> memberService.update(nonExistingUpdateMember));
//            assertEquals("999(으)로 등록된 회원이 없습니다.", exception.getMessage());
//        }
//
//        @Test
//        void update_NoChanges_ThrowsDataIntegrityViolationException(){
//            Member noChangeMember = new Member();
//            noChangeMember.setId(member.getId());
//
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//            when(passwordEncoder.matches("samePassword", member.getPassword())).thenReturn(true);
//
//            NoChangeException exception = assertThrows(NoChangeException.class, () -> memberService.update(noChangeMember));
//            assertEquals("변경된 회원 정보가 없습니다.", exception.getMessage());
//
//        }
//
//
//    }
//
//    @Nested
//    class DeleteByIdTests {
//        @Test
//        void deleteById_ExistingMember_DeletesSuccessfully() {
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
//
//            memberService.deleteById(member.getId());
//
//            verify(memberRepository).deleteById(member.getId());
//        }
//
//        @Test
//        void deleteById_NonExistingMember_ThrowsIllegalArgumentException() {
//            when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());
//
//            IdNotFoundException exception = assertThrows(IdNotFoundException.class, () -> memberService.deleteById(member.getId()));
//            assertEquals(member.getId() + "(으)로 등록된 회원이 없습니다.", exception.getMessage());
//        }
//    }*/
//}
