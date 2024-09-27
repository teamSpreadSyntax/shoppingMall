package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.domain.RoleType;
import home.project.repository.MemberRepository;
import home.project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new UsernameNotFoundException(email + "(으)로 등록된 회원이 없습니다.");
        });
        Role role = roleService.findById(member.getId());
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role.getRole() == RoleType.admin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (role.getRole() == RoleType.user) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if (role.getRole() == RoleType.center) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CENTER"));
        }
        User userDetails = new User(member.getEmail(), member.getPassword(), authorities);
        return userDetails;
    }

}
