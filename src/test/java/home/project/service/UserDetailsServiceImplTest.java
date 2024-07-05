package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.domain.UserDetailsDTO;
import home.project.repository.MemberRepository;
import home.project.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    RoleRepository roleRepository;

    UserDetailsDTO userDetailsDTO;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    private Member member;
    private Role role;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("test@test.com");
        member.setPassword("password");

        role = new Role();
        role.setId(1L);
        role.setRole("user");
    }

    @Nested
    class loadUserByUsernameTests {
        @Test
        void 유저에게_권한부여_성공() {
            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
            when(roleRepository.findById(member.getId())).thenReturn(Optional.of(role));

            UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

            assertNotNull(userDetails);
            assertEquals("test@test.com", userDetails.getUsername());
            assertEquals("password", userDetails.getPassword());
            assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
        }

        @Test
        void 이메일_검색_실패() {
            when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("test@test.com"));
            assertEquals("test@test.com로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }
}