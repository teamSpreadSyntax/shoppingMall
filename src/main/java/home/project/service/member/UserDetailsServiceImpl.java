package home.project.service.member;

import home.project.domain.member.Member;
import home.project.repository.member.MemberRepository;
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

import static home.project.domain.member.RoleType.*;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new UsernameNotFoundException(email + "(으)로 등록된 회원이 없습니다.");
        });
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getRole().equals(admin)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (member.getRole().equals(user)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if (member.getRole().equals(center)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CENTER"));
        }
        return new User(member.getEmail(), member.getPassword(), authorities);
    }

}
