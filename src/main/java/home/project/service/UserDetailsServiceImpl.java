package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserDetailsServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)throws UsernameNotFoundException {
      Member member =  memberRepository.findByEmail(username).orElseThrow(() -> { throw new IllegalStateException(username+"로 가입된 회원이 없습니다."); });
                List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        User userDetails = new User(member.getEmail(), member.getPassword(), authorities);
        return userDetails;
//        return new org.springframework.security.core.userdetails.User(member.getEmail(), passwordEncoder.encode(member.getPassword()), authorities);
//        return new org.springframework.security.core.userdetails.User(
//                member.getEmail(), member.getPassword(), new ArrayList<>());
    }

}
