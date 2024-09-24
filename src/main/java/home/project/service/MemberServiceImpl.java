package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.dto.MemberDTOWithoutId;
import home.project.dto.TokenDto;
import home.project.exceptions.IdNotFoundException;
import home.project.exceptions.NoChangeException;
import home.project.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtTokenProvider jwtTokenProvider;


    public Member convertToEntity(MemberDTOWithoutId memberDTOWithoutId) {
        Member member = new Member();
        member.setEmail(memberDTOWithoutId.getEmail());
        member.setPassword(passwordEncoder.encode(memberDTOWithoutId.getPassword()));
        member.setName(memberDTOWithoutId.getName());
        member.setPhone(memberDTOWithoutId.getPhone());
        return member;
    }
    @Transactional
    public Optional<Map<String, String>> join(MemberDTOWithoutId memberDTO) {
        Member member = convertToEntity(memberDTO);
        boolean emailExists = memberRepository.existsByEmail(member.getEmail());
        boolean phoneExists = memberRepository.existsByPhone(member.getPhone());
        if (emailExists && phoneExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일과 전화번호입니다.");
        } else if (emailExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일입니다.");
        } else if (phoneExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 전화번호입니다.");
        }
        memberRepository.save(member);
        Optional<Member> memberForAddRole = memberRepository.findByEmail(member.getEmail());
        Role role = new Role();
        Long id = memberForAddRole.get().getId();
        role.setId(id);
        roleService.join(role);
        TokenDto tokenDto = generateToken(member.getEmail(),member.getPassword());
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("accessToken", tokenDto.getAccessToken());
        responseMap.put("refreshToken", tokenDto.getRefreshToken());
        responseMap.put("role", role.getRole());
        responseMap.put("successMessage", "회원가입이 성공적으로 완료되었습니다.");
        return Optional.of(responseMap);
    }

    public TokenDto generateToken(String email, String password){
        return jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(email,password));
    }

    public Optional<Member> findById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다.");
        });

        return Optional.ofNullable(member);
    }

    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> { throw new IdNotFoundException(email+"(으)로 등록된 회원이 없습니다."); });
        return Optional.ofNullable(member);
    }

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Page<Member> findMembers(String name, String email, String phone, String role, String content, Pageable pageable) {
        return memberRepository.findMembers(name, email, phone, role, content, pageable);
    }

    public Optional<Member> update(Member member) {
        Member existingMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IdNotFoundException(member.getId() + "(으)로 등록된 회원이 없습니다."));
        boolean isModified = false;
        boolean isEmailDuplicate = false;
        boolean isPhoneDuplicate = false;

        if (member.getName() != null && !Objects.equals(existingMember.getName(), member.getName())) {
            existingMember.setName(member.getName());
            isModified = true;
        }
        if (member.getEmail() != null && !Objects.equals(existingMember.getEmail(), member.getEmail())) {
            if (memberRepository.existsByEmail(member.getEmail())) {
                isEmailDuplicate = true;
            } else {
                existingMember.setEmail(member.getEmail());
                isModified = true;
            }
        }

        if (member.getPhone() != null && !Objects.equals(existingMember.getPhone(), member.getPhone())) {
            if (memberRepository.existsByPhone(member.getPhone())) {
                isPhoneDuplicate = true;
            } else {
                existingMember.setPhone(member.getPhone());
                isModified = true;
            }
        }

        if (member.getPassword() != null && !passwordEncoder.matches(member.getPassword(), existingMember.getPassword())) {
            existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
            isModified = true;
        }

        if (isEmailDuplicate && isPhoneDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일과 전화번호입니다.");
        } else if (isEmailDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일입니다.");
        } else if (isPhoneDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 전화번호입니다.");
        }

        if (!isModified) {
            throw new NoChangeException("변경된 회원 정보가 없습니다.");
        }

        return Optional.of(memberRepository.save(existingMember));
    }

    public void deleteById(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다."));
        memberRepository.deleteById(memberId);
    }

}
