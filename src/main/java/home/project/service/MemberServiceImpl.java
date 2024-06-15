package home.project.service;

import home.project.domain.Member;
import home.project.domain.MemberDTOWithoutId;
import home.project.domain.MemberDTOWithoutPw;
import home.project.domain.Product;
import home.project.repository.MemberRepository;
//import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        boolean emailExists = memberRepository.existsByEmail(member.getEmail());
        boolean phoneExists = memberRepository.existsByPhone(member.getPhone());
        if (emailExists && phoneExists) {
            throw new DataIntegrityViolationException("이메일과 휴대폰번호가 모두 중복됩니다.");
        } else if (emailExists) {
            throw new DataIntegrityViolationException("이메일이 중복됩니다.");
        } else if (phoneExists) {
            throw new DataIntegrityViolationException("휴대폰번호가 중복됩니다.");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    public Optional<Member> findById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> { throw new IllegalArgumentException(id+"로 등록된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> { throw new IllegalArgumentException(email+"로 가입된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
    public Page<Member> findMembers(String name, String email, String phone,String query, Pageable pageable) {
        Page<Member> memberPage = memberRepository.findMembers(name, email, phone, query, pageable);
        if (memberPage.getSize()==0||memberPage.getTotalElements()==0) {
            throw new IllegalArgumentException("해당하는 회원을 찾을 수 없습니다.");
        }
        return memberRepository.findMembers(name, email, phone, query, pageable);

    }

    public Optional<Member> update (Member member){
        Member exsitsMember = memberRepository.findById(member.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        boolean emailExists = memberRepository.existsByEmail(member.getEmail());
        boolean phoneExists = memberRepository.existsByPhone(member.getPhone());
        if (emailExists && phoneExists) {
            throw new DataIntegrityViolationException("이메일과 휴대폰번호가 모두 중복됩니다.");
        } else if (emailExists) {
            throw new DataIntegrityViolationException("이메일이 중복됩니다.");
        } else if (phoneExists) {
            throw new DataIntegrityViolationException("휴대폰번호가 중복됩니다.");
        }
        exsitsMember.setName(member.getName());
        exsitsMember.setEmail(member.getEmail());
        exsitsMember.setPhone(member.getPhone());
        exsitsMember.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(exsitsMember);
        Optional<Member> newMember = memberRepository.findById(exsitsMember.getId());
        return newMember;
    }

    public void deleteMember(Long memberId){
        memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberRepository.deleteById(memberId);
    }

}
