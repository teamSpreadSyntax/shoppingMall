/*

package home.project.service;

import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserDetailsServiceImplTest {

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("test@test.com");
        member.setPassword("password");
        member.setRole(RoleType.user);
    }

    @Nested
    class loadUserByUsernameTests {
        @Test
        void loadUserByUsername_ExistingEmail_ReturnsUserDetails() {
            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

            UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

            assertNotNull(userDetails);
            assertEquals(member.getEmail(), userDetails.getUsername());
            assertEquals(member.getPassword(), userDetails.getPassword());
            assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        }

        @Test
        void loadUserByUsername_AdminExists_ReturnsUserDetailsWithAdminRole() {
            member.setRole(RoleType.admin);
            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

            UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

            assertNotNull(userDetails);
            assertTrue(userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }

        @Test
        void loadUserByUsername_CenterExists_ReturnsUserDetailsWithCenterRole() {
            member.setRole(RoleType.center);
            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

            UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

            assertNotNull(userDetails);
            assertTrue(userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CENTER")));
        }

        @Test
        void loadUserByUsername_NonExistingEmail_ThrowsUsernameNotFoundException() {
            when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("test@test.com"));
            assertEquals("test@test.com(으)로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }
}
*/
