package home.project.service;

import home.project.domain.Member;
import home.project.repository.MemberRepository;
//import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final  OAuth2ResourceServerProperties.Jwt jwt;
@Autowired
    public  MemberServiceImpl(MemberRepository memberRepository,PasswordEncoder passwordEncoder/*, AuthenticationManagerBuilder authenticationManagerBuilder, OAuth2ResourceServerProperties.Jwt jwt*/){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void join (Member member){
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> { throw new IllegalStateException(email+"로 가입된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Optional<Member> update (Member member){
        Member exsitsMember = memberRepository.findByEmail(member.getEmail()).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));
        exsitsMember.setPassword(member.getPassword());
        exsitsMember.setName(member.getName());
        exsitsMember.setEmail(member.getEmail());
        exsitsMember.setPhone(member.getPhone());
        memberRepository.save(exsitsMember);
        Optional<Member> newMember = memberRepository.findById(exsitsMember.getId());
        return newMember;
    }

    public void deleteMember(String email){
        memberRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));
        memberRepository.deleteByEmail(email);
    }

}
