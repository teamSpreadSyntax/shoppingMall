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
    private UserDetailsServiceImpl userDetailsService;
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
//    @Nested
//    class loadUserByUsername {
//        @Test
//        void 유저에게_권한부여_성공() {
//            when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(Member));
//        }
//    }


}