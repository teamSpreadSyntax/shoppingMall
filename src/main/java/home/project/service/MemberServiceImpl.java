package home.project.service;

import home.project.domain.Member;
import home.project.domain.MemberDTOWithoutId;
import home.project.domain.MemberDTOWithoutPw;
import home.project.repository.MemberRepository;
//import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Optional<Member> findById(Long ID) {
        Member member = memberRepository.findById(ID).orElseThrow(() -> { throw new IllegalArgumentException(ID+"로 등록된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> { throw new IllegalArgumentException(email+"로 가입된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Optional<Member> update (Member member){
        Member exsitsMember = memberRepository.findByEmail(member.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        exsitsMember.setPassword(member.getPassword());
        exsitsMember.setName(member.getName());
        exsitsMember.setEmail(member.getEmail());
        exsitsMember.setPhone(member.getPhone());
        memberRepository.save(exsitsMember);
        Optional<Member> newMember = memberRepository.findById(exsitsMember.getId());
        return newMember;
    }

    public void deleteMember(String email){
        memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberRepository.deleteByEmail(email);
    }

}
