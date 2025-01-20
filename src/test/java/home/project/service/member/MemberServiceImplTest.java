package home.project.service.member;

import home.project.domain.elasticsearch.MemberDocument;
import home.project.domain.member.Member;
import home.project.domain.member.MemberGenderType;
import home.project.domain.member.MemberGradeType;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.CreateSocialMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.MemberResponseForUser;
import home.project.dto.responseDTO.TokenResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.member.MemberRepository;
import home.project.repositoryForElasticsearch.MemberElasticsearchRepository;
import home.project.service.util.Converter;
import home.project.service.util.EmailService;
import home.project.service.util.IndexToElasticsearch;
import home.project.service.util.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private CreateSocialMemberRequestDTO socialJoinRequest;
    private UpdateMemberRequestDTO updateRequest;
    private TokenResponse tokenResponse;
    private MemberResponse memberResponse;
    private String testEmail;
    private String testPassword;

    @BeforeEach
    void setUp() {
        // 기본 데이터 설정
        testEmail = "test@test.com";
        testPassword = "Password123!";

        // 회원가입 요청 데이터 설정
        joinRequest = new CreateMemberRequestDTO();
        joinRequest.setEmail(testEmail);
        joinRequest.setPassword(testPassword);
        joinRequest.setPasswordConfirm(testPassword);
        joinRequest.setName("테스트");
        joinRequest.setPhone("01012345678");
        joinRequest.setGender(MemberGenderType.M);
        joinRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        joinRequest.setDefaultAddress("서울시 강남구");

        // 소셜 회원가입 요청 데이터 설정
        socialJoinRequest = new CreateSocialMemberRequestDTO();
        socialJoinRequest.setEmail(testEmail);
        socialJoinRequest.setName("테스트");
        socialJoinRequest.setPhone("01012345678");
        socialJoinRequest.setGender(MemberGenderType.M);
        socialJoinRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        socialJoinRequest.setDefaultAddress("서울시 강남구");

        // 회원 정보 수정 요청 데이터 설정
        updateRequest = new UpdateMemberRequestDTO();
        updateRequest.setName("수정된이름");
        updateRequest.setPassword("NewPassword123!");
        updateRequest.setPasswordConfirm("NewPassword123!");
        updateRequest.setPhone("01087654321");

        // Member 엔티티 설정
        testMember = new Member();
        testMember.setId(1L);
        testMember.setEmail(testEmail);
        testMember.setPassword("encodedPassword");
        testMember.setName("테스트");
        testMember.setPhone("01012345678");
        testMember.setRole(RoleType.user);
        testMember.setGender(MemberGenderType.M);
        testMember.setBirthDate(LocalDate.of(1990, 1, 1));
        testMember.setDefaultAddress("서울시 강남구");
        testMember.setGrade(MemberGradeType.SILVER);
        testMember.setPoint(1000L);

        // TokenResponse 설정
        tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("test.access.token");
        tokenResponse.setRefreshToken("test.refresh.token");
        tokenResponse.setRole(RoleType.user);

        // MemberResponse 설정
        memberResponse = new MemberResponse(
                1L,
                testEmail,
                "테스트",
                "01012345678",
                RoleType.user,
                MemberGenderType.M,
                LocalDate.of(1990, 1, 1),
                "서울시 강남구",
                null,
                null,
                MemberGradeType.SILVER,
                1000L,
                new ArrayList<>()
        );
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class JoinTest {

        @Test
        @DisplayName("정상적인 회원가입 요청시 성공한다")
        void joinSuccess() {
            // given
            when(memberRepository.existsByEmail(anyString())).thenReturn(false);
            when(memberRepository.existsByPhone(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(converter.convertFromCreateMemberRequestDTOToMember(any())).thenReturn(testMember);
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);
            when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
            when(authenticationManager.authenticate(any())).thenReturn(
                    new UsernamePasswordAuthenticationToken(testMember.getEmail(), testMember.getPassword())
            );
            when(jwtTokenProvider.generateToken(any())).thenReturn(tokenResponse);
            doNothing().when(indexToElasticsearch).indexDocumentToElasticsearch(any(), any());

            // when
            TokenResponse result = memberService.join(joinRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
            verify(memberRepository).save(any(Member.class));
            verify(converter).convertFromMemberToMemberDocument(any(Member.class));
            verify(memberRepository).findById(testMember.getId());
            verify(indexToElasticsearch).indexDocumentToElasticsearch(any(), any());
        }

        @Test
        @DisplayName("이미 가입된 이메일로 가입시 실패한다")
        void joinFailDuplicateEmail() {
            // given
            when(memberRepository.existsByEmail(anyString())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.join(joinRequest))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessage("이미 사용 중인 이메일입니다.");

            verify(memberRepository, never()).save(any());
            verify(converter, never()).convertFromMemberToMemberDocument(any());
        }
    }

    @Nested
    @DisplayName("소셜 회원가입 테스트")
    class SocialJoinTest {

        @Test
        @DisplayName("정상적인 소셜 회원가입 요청시 성공한다")
        void socialJoinSuccess() {
            // given
            when(memberRepository.existsByEmail(anyString())).thenReturn(false);
            when(memberRepository.existsByPhone(anyString())).thenReturn(false);
            when(converter.convertFromCreateMemberRequestDTOToMember(any())).thenReturn(testMember);
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);
            when(authenticationManager.authenticate(any())).thenReturn(
                    new UsernamePasswordAuthenticationToken(testMember.getEmail(), testMember.getPassword())
            );
            when(jwtTokenProvider.generateToken(any())).thenReturn(tokenResponse);
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
            doNothing().when(indexToElasticsearch).indexDocumentToElasticsearch(any(), any());

            // when
            TokenResponse result = memberService.socialJoin(socialJoinRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(tokenResponse.getAccessToken());
            verify(memberRepository).save(any(Member.class));
        }
    }

    @Nested
    @DisplayName("회원 정보 조회 테스트")
    class MemberInfoTest {

        @Test
        @DisplayName("정상적으로 회원 정보를 조회한다")
        void memberInfoSuccess() {
            // given
            Authentication auth = new UsernamePasswordAuthenticationToken(testEmail, null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(memberRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMember));
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(converter.convertFromMemberToMemberResponse(testMember)).thenReturn(memberResponse);

            // when
            MemberResponse result = memberService.memberInfo();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(testEmail);
            verify(memberRepository).findByEmail(testEmail);
        }

        @Test
        @DisplayName("존재하지 않는 회원 정보 조회시 실패한다")
        void memberInfoFailNotFound() {
            // given
            Authentication auth = new UsernamePasswordAuthenticationToken(testEmail, null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(memberRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.memberInfo())
                    .isInstanceOf(IdNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class UpdateTest {

        @Test
        @DisplayName("정상적으로 회원 정보를 수정한다")
        void updateSuccess() {
            // given
            String verificationToken = "valid.token";
            when(jwtTokenProvider.getEmailFromToken(verificationToken)).thenReturn(testEmail);
            when(jwtTokenProvider.getIdFromVerificationToken(verificationToken)).thenReturn("1");
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(passwordEncoder.matches(any(), any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("newEncodedPassword");
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);
            doNothing().when(indexToElasticsearch).indexDocumentToElasticsearch(any(), any());

            // when
            MemberResponseForUser result = memberService.update(updateRequest, verificationToken);

            // then
            assertThat(result).isNotNull();
            verify(memberRepository).save(any(Member.class));
            verify(indexToElasticsearch).indexDocumentToElasticsearch(any(), any());
        }
    }

    @Nested
    @DisplayName("회원 검색 테스트")
    class SearchTest {

        @Test
        @DisplayName("조건에 맞는 회원을 검색한다")
        void searchSuccess() {
            // given
            Page<Member> memberPage = new PageImpl<>(List.of(testMember));
            Page<MemberResponse> expectedResponse = new PageImpl<>(List.of(memberResponse));
            Pageable pageable = PageRequest.of(0, 10);

            when(memberRepository.findMembers(anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                    .thenReturn(memberPage);
            when(converter.convertFromPagedMemberToPagedMemberResponse(any()))
                    .thenReturn(expectedResponse);

            // when
            Page<MemberResponse> result = memberService.findMembers(
                    "name", "email", "phone", "role", "content", pageable
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isNotNull();
            assertThat(result.getContent().get(0).getEmail()).isEqualTo(testEmail);
        }
    }

    @Test
    @DisplayName("정상적으로 회원을 삭제한다")
    void deleteSuccess() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        // when
        String result = memberService.deleteById(1L);

        // then
        assertThat(result).isEqualTo(testEmail);
        verify(memberRepository).deleteById(1L);
        verify(elasticsearchOperations).delete("1", MemberDocument.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}