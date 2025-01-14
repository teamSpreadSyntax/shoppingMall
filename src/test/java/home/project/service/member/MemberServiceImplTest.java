package home.project.service.member;

import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.*;
import home.project.dto.responseDTO.*;
import home.project.exceptions.exception.*;
import home.project.repository.member.MemberRepository;
import home.project.repositoryForElasticsearch.MemberElasticsearchRepository;
import home.project.service.common.EmailService;
import home.project.service.integration.IndexToElasticsearch;
import home.project.service.security.JwtTokenProvider;
import home.project.service.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Converter converter;

    @Mock
    private IndexToElasticsearch indexToElasticsearch;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private MemberElasticsearchRepository memberElasticsearchRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member testMember;
    private CreateMemberRequestDTO joinRequest;
    private UpdateMemberRequestDTO updateRequest;
    private TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail("test@example.com");
        testMember.setPassword("encodedPassword");
        testMember.setRole(RoleType.user);

        joinRequest = new CreateMemberRequestDTO();
        joinRequest.setEmail("test@example.com");
        joinRequest.setPassword("password");
        joinRequest.setPasswordConfirm("password");

        updateRequest = new UpdateMemberRequestDTO();
        updateRequest.setName("New Name");
        updateRequest.setPassword("newPassword");
        updateRequest.setPasswordConfirm("newPassword");

        tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("access-token");
        tokenResponse.setRefreshToken("refresh-token");
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class JoinTest {

        @Test
        @DisplayName("정상적인 회원가입 요청 시 성공한다")
        void joinSuccess() {
            when(memberRepository.existsByEmail(anyString())).thenReturn(false);
            when(memberRepository.existsByPhone(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(converter.convertFromCreateMemberRequestDTOToMember(any())).thenReturn(testMember);
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);
            when(jwtTokenProvider.generateToken(any())).thenReturn(tokenResponse);
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));

            TokenResponse result = memberService.join(joinRequest);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("access-token");
            verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("이미 가입된 이메일로 회원가입 요청 시 실패한다")
        void joinFailDuplicateEmail() {
            when(memberRepository.existsByEmail(anyString())).thenReturn(true);

            assertThatThrownBy(() -> memberService.join(joinRequest))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessageContaining("이미 사용 중인 이메일");

            verify(memberRepository, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("비밀번호와 비밀번호 확인 불일치 시 실패한다")
        void joinFailPasswordMismatch() {
            joinRequest.setPasswordConfirm("differentPassword");

            assertThatThrownBy(() -> memberService.join(joinRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("비밀번호와 비밀번호 확인이 일치하지 않습니다");

            verify(memberRepository, never()).save(any(Member.class));
        }
    }

    @Nested
    @DisplayName("회원 정보 조회 테스트")
    class MemberInfoTest {

        @Test
        @DisplayName("정상적으로 회원 정보를 조회한다")
        void memberInfoSuccess() {
            when(memberRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(testMember));

            when(memberRepository.findById(testMember.getId()))
                    .thenReturn(Optional.of(testMember));

            MemberResponse mockResponse = new MemberResponse(
                    1L, "test@example.com", "Test Name", "01012345678", RoleType.user,
                    null, null, "Default Address", "Second Address", "Third Address",
                    null, 100L, List.of()
            );
            when(converter.convertFromMemberToMemberResponse(testMember))
                    .thenReturn(mockResponse);

            MemberResponse result = memberService.memberInfo();

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(memberRepository).findByEmail("test@example.com");
            verify(memberRepository).findById(testMember.getId());
        }

        @Test
        @DisplayName("회원 정보 조회 실패: 존재하지 않는 회원")
        void memberInfoFailNotFound() {
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.memberInfo())
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 회원이 없습니다");

            verify(memberRepository).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class UpdateTest {

        @Test
        @DisplayName("회원 정보 수정 성공")
        void updateSuccess() {
            String token = "valid-token";
            when(jwtTokenProvider.getEmailFromToken(token)).thenReturn("test@example.com");
            when(jwtTokenProvider.getIdFromVerificationToken(token)).thenReturn("1");
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);

            MemberResponseForUser result = memberService.update(updateRequest, token);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(updateRequest.getName());
            verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("회원 정보 수정 실패: 변경사항 없음")
        void updateFailNoChanges() {
            String token = "valid-token";

            // Mock 설정
            when(jwtTokenProvider.getEmailFromToken(token)).thenReturn("test@example.com");
            when(jwtTokenProvider.getIdFromVerificationToken(token)).thenReturn("1"); // ID 반환 설정
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true); // 기존 비밀번호와 동일하게 설정

            // 변경사항이 없도록 설정
            updateRequest.setName(testMember.getName());
            updateRequest.setPassword("encodedPassword"); // 기존 비밀번호 설정
            updateRequest.setPasswordConfirm("encodedPassword"); // 비밀번호 확인도 동일하게 설정

            // 테스트
            assertThatThrownBy(() -> memberService.update(updateRequest, token))
                    .isInstanceOf(NoChangeException.class)
                    .hasMessageContaining("변경된 회원 정보가 없습니다");

            // save 메서드가 호출되지 않았는지 확인
            verify(memberRepository, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("회원 정보 수정 실패: 중복 이메일 존재")
        void updateFailDuplicateEmail() {
            String token = "valid-token";
            when(jwtTokenProvider.getEmailFromToken(token)).thenReturn("test@example.com");
            when(jwtTokenProvider.getIdFromVerificationToken(token)).thenReturn("1");
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
            when(memberRepository.existsByEmail(anyString())).thenReturn(true);

            updateRequest.setEmail("duplicate@example.com");

            assertThatThrownBy(() -> memberService.update(updateRequest, token))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessageContaining("이미 사용 중인 이메일");
        }


        @Test
        @DisplayName("회원 정보 수정 실패: 중복 전화번호 존재")
        void updateFailDuplicatePhone() {
            String token = "valid-token";
            when(jwtTokenProvider.getEmailFromToken(token)).thenReturn("test@example.com");
            when(jwtTokenProvider.getIdFromVerificationToken(token)).thenReturn("1"); // 추가 설정
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
            when(memberRepository.existsByPhone(anyString())).thenReturn(true);

            updateRequest.setPhone("01099999999");

            assertThatThrownBy(() -> memberService.update(updateRequest, token))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessageContaining("이미 사용 중인 전화번호");
        }

        @Test
        @DisplayName("회원 정보 수정 실패: 비밀번호 확인 불일치")
        void updateFailPasswordMismatch() {
            String token = "valid-token";
            when(jwtTokenProvider.getEmailFromToken(token)).thenReturn("test@example.com");
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));

            updateRequest.setPasswordConfirm("differentPassword");

            assertThatThrownBy(() -> memberService.update(updateRequest, token))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }
    }

    @Nested
    @DisplayName("회원 삭제 테스트")
    class DeleteTest {

        @Test
        @DisplayName("회원 삭제 성공")
        void deleteSuccess() {
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));

            String result = memberService.deleteById(1L);

            assertThat(result).isEqualTo("test@example.com");
            verify(memberRepository).deleteById(anyLong());
        }

        @Test
        @DisplayName("회원 삭제 실패: 회원 존재하지 않음")
        void deleteFailMemberNotFound() {
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.deleteById(1L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessageContaining("등록된 회원이 없습니다");
        }
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
