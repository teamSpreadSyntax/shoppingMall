package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.dto.UserDetailsDTO;
import home.project.repository.MemberRepository;
import home.project.repository.RoleRepository;
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

    @MockBean
    RoleRepository roleRepository;

    UserDetailsDTO userDetailsDTO;

    @Autowired
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
        void loadUserByUsername_LoadUserSuccessfully_GrantedAuthoritiesToUser() {
            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
            when(roleRepository.findById(member.getId())).thenReturn(Optional.of(role));

            UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

            assertNotNull(userDetails);
            assertEquals(member.getEmail(), userDetails.getUsername());
            assertEquals(member.getPassword(), userDetails.getPassword());
            assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
        }

        @Test
        void loadUserByUsername_EmailNotFound_ThrowsUsernameNotFoundException() {
            when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("test@test.com"));
            assertEquals("test@test.com(으)로 등록된 회원이 없습니다.", exception.getMessage());
        }
    }
}