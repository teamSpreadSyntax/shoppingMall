package home.project.service.member;

import home.project.domain.member.Member;
import home.project.domain.member.MemberGenderType;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.responseDTO.TokenResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.member.MemberRepository;
import home.project.repositoryForElasticsearch.MemberElasticsearchRepository;
import home.project.service.util.Converter;
import home.project.service.util.IndexToElasticsearch;
import home.project.service.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;

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
    private MemberElasticsearchRepository memberElasticsearchRepository;

    private Member member;
    private CreateMemberRequestDTO createMemberRequestDTO;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        // CreateMemberRequestDTO 초기화
        createMemberRequestDTO = new CreateMemberRequestDTO();
        createMemberRequestDTO.setEmail("test@example.com");
        createMemberRequestDTO.setPassword("password123");
        createMemberRequestDTO.setPasswordConfirm("password123");
        createMemberRequestDTO.setName("John Doe");
        createMemberRequestDTO.setPhone("01012345678");
        createMemberRequestDTO.setGender(MemberGenderType.M);
        createMemberRequestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        createMemberRequestDTO.setDefaultAddress("123 Test St.");

        // Member 초기화
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPassword("encodedPassword");
        member.setName("John Doe");
        member.setPhone("01012345678");
        member.setGender(MemberGenderType.M);
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        member.setDefaultAddress("123 Test St.");
    }

    @Test
    void join_ValidRequest_Success() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // mock memberRepository.save()가 member 객체를 반환하도록 설정
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        TokenResponse tokenResponse = memberService.join(createMemberRequestDTO);

        // Then
        assertNotNull(tokenResponse);
        assertEquals("encodedPassword", member.getPassword());  // 암호화된 비밀번호 확인
        assertEquals("test@example.com", member.getEmail());    // 이메일이 정확한지 확인
        verify(memberRepository, times(1)).save(any());  // memberRepository.save()가 한 번 호출됐는지 확인
    }

    @Test
    void join_PasswordMismatch_Failure() {
        // Given
        createMemberRequestDTO.setPasswordConfirm("password456"); // Mismatch

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            memberService.join(createMemberRequestDTO);
        });
        assertEquals("비밀번호와 비밀번호 확인이 일치하지 않습니다.", exception.getMessage());
    }
}
