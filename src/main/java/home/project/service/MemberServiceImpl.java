package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder/*, AuthenticationManagerBuilder authenticationManagerBuilder, OAuth2ResourceServerProperties.Jwt jwt*/) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void join(Member member) {
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

    public Optional<Member> findById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IllegalArgumentException(memberId + "로 등록된 회원이 없습니다.");
        });
        return Optional.ofNullable(member);
    }

    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> { throw new IllegalArgumentException(email+"로 등록된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Page<Member> findMembers(String name, String email, String phone, String content, Pageable pageable) {
        Page<Member> memberPage = memberRepository.findMembers(name, email, phone, content, pageable);
        if (memberPage.getSize() == 0 || memberPage.getTotalElements() == 0) {
            throw new IllegalArgumentException("해당하는 회원이 없습니다.");
        }
        return memberRepository.findMembers(name, email, phone, content, pageable);
    }

    public Optional<Member> update(Member member) {

        Member existsMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException(member.getId() + "로 등록된 회원이 없습니다."));

        if (member.getName() != null) {
            existsMember.setName(member.getName());
        }
        if (member.getEmail() != null) {

            if (!member.getEmail().equals(existsMember.getEmail())) {
                boolean emailExists = memberRepository.existsByEmail(member.getEmail());
                if (emailExists) {
                    throw new DataIntegrityViolationException("이미 사용 중인 이메일입니다.");
                }
                existsMember.setEmail(member.getEmail());
            }
        }
        if (member.getPhone() != null) {

            if (!member.getPhone().equals(existsMember.getPhone())) {
                boolean phoneExists = memberRepository.existsByPhone(member.getPhone());
                if (phoneExists) {
                    throw new DataIntegrityViolationException("이미 사용 중인 휴대폰번호입니다.");
                }
                existsMember.setPhone(member.getPhone());
            }
        }
        if (member.getPassword() != null) {
            existsMember.setPassword(passwordEncoder.encode(member.getPassword()));
        }

        memberRepository.save(existsMember);

        return Optional.of(existsMember);
    }




    public void deleteById(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException(memberId + "로 등록된 회원이 없습니다."));
        memberRepository.deleteById(memberId);
    }

    public void logout(Long memberId) {
    }

    ;

}
