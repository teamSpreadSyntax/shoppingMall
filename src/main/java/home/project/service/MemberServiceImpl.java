package home.project.service;

import home.project.domain.Member;
import home.project.domain.MemberDTOWithoutId;
import home.project.domain.Role;
import home.project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
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
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$");
    }
    public Member convertToEntity(MemberDTOWithoutId memberDTOWithoutId) {
        System.out.println("11");
        Member member = new Member();
        member.setEmail(memberDTOWithoutId.getEmail());
        System.out.println("22");
        member.setPassword(passwordEncoder.encode(memberDTOWithoutId.getPassword()));
        System.out.println("33");
        member.setName(memberDTOWithoutId.getName());
        System.out.println("44");
        member.setPhone(memberDTOWithoutId.getPhone());
        System.out.println("55");
        if (!isValidPassword(memberDTOWithoutId.getPassword())) {
        System.out.println("66");
            throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다.");
        }
        return member;
    }

    public void join(Member member) {
        System.out.println("77");
        boolean emailExists = memberRepository.existsByEmail(member.getEmail());
        System.out.println("88");
        boolean phoneExists = memberRepository.existsByPhone(member.getPhone());
        System.out.println("99");
        if (emailExists && phoneExists) {
        System.out.println("1010");
            throw new DataIntegrityViolationException("이메일과 휴대폰번호가 모두 중복됩니다.");
        } else if (emailExists) {
        System.out.println("1111");
            throw new DataIntegrityViolationException("이메일이 중복됩니다.");
        } else if (phoneExists) {
        System.out.println("1212");
            throw new DataIntegrityViolationException("휴대폰번호가 중복됩니다.");
        }
        System.out.println("1313");
//        member.setPassword(passwordEncoder.encode(member.getPassword()));
        System.out.println("1414");
        memberRepository.save(member);
        System.out.println("1515");
    }

    public Optional<Member> findById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IllegalArgumentException(memberId + "(으)로 등록된 회원이 없습니다.");
        });
        return Optional.ofNullable(member);
    }

    public Optional<Member> findByEmail(String email) {
        System.out.println("1616");
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> { throw new IllegalArgumentException(email+"(으)로 등록된 회원이 없습니다."); });
        System.out.println("1717");
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
        Member existingMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException(member.getId() + "(으)로 등록된 회원이 없습니다."));

        boolean isModified = false;

        if (member.getName() != null && !Objects.equals(existingMember.getName(), member.getName())) {
            existingMember.setName(member.getName());
            isModified = true;
        }

        if (member.getEmail() != null && !Objects.equals(existingMember.getEmail(), member.getEmail())) {
            if (memberRepository.existsByEmail(member.getEmail())) {
                throw new DataIntegrityViolationException("이미 사용 중인 이메일입니다.");
            }
            existingMember.setEmail(member.getEmail());
            isModified = true;
        }

        if (member.getPhone() != null && !Objects.equals(existingMember.getPhone(), member.getPhone())) {
            if (memberRepository.existsByPhone(member.getPhone())) {
                throw new DataIntegrityViolationException("이미 사용 중인 휴대폰번호입니다.");
            }
            existingMember.setPhone(member.getPhone());
            isModified = true;
        }

        if (member.getPassword() != null && !passwordEncoder.matches(member.getPassword(), existingMember.getPassword())) {
            existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
            isModified = true;
        }

        if (!isModified) {
            throw new DataIntegrityViolationException("변경된 회원 정보가 없습니다.");
        }

        return Optional.of(memberRepository.save(existingMember));
    }

    public void deleteById(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException(memberId + "(으)로 등록된 회원이 없습니다."));
        memberRepository.deleteById(memberId);
    }

    public void logout(Long memberId) {
    }

}
