package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.dto.*;
import home.project.exceptions.IdNotFoundException;
import home.project.exceptions.NoChangeException;
import home.project.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder, RoleService roleService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider/*, AuthenticationManagerBuilder authenticationManagerBuilder, OAuth2ResourceServerProperties.Jwt jwt*/) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Member convertToEntity(MemberDTOWithoutId memberDTOWithoutId) {
        Member member = new Member();
        member.setEmail(memberDTOWithoutId.getEmail());
        member.setPassword(passwordEncoder.encode(memberDTOWithoutId.getPassword()));
        member.setName(memberDTOWithoutId.getName());
        member.setPhone(memberDTOWithoutId.getPhone());
        return member;
    }

    public TokenDto join(MemberDTOWithoutId memberDTO) {

        if (!memberDTO.getPassword().equals(memberDTO.getPasswordConfirm())) {
            throw new IllegalStateException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        boolean emailExists = memberRepository.existsByEmail(memberDTO.getEmail());
        boolean phoneExists = memberRepository.existsByPhone(memberDTO.getPhone());
        if (emailExists && phoneExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일과 전화번호입니다.");
        } else if (emailExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일입니다.");
        } else if (phoneExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 전화번호입니다.");
        }

        Member member = convertToEntity(memberDTO);
        memberRepository.save(member);
        Optional<Member> memberForAddRole = findByEmail(member.getEmail());
        Long id = memberForAddRole.get().getId();

        Role role = new Role();
        role.setId(id);
        roleService.join(role);

        String savedRole = roleService.findById(id).get().getRole();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(memberDTO.getEmail(), memberDTO.getPassword()));
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        tokenDto.setRole(savedRole);

        return tokenDto;
    }

    public Optional<MemberDTOWithoutPw> memberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long memberId = findByEmail(email).get().getId();
        Optional<Member> memberOptional = findById(memberId);
        String role = roleService.findById(memberId).get().getRole();
        Optional<MemberDTOWithoutPw> memberDTOWithoutPw = memberOptional.map(member -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), role));
        return memberDTOWithoutPw;
    }

    public Optional<Member> findById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다.");
        });
        return Optional.ofNullable(member);
    }

    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new IdNotFoundException(email + "(으)로 등록된 회원이 없습니다.");
        });
        return Optional.ofNullable(member);
    }

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Page<MemberDTOWithoutPw> convertToMemberDTOWithoutPW(Page<Member> memberPage) {
        Page<MemberDTOWithoutPw> pagedMemberDTOWithoutPw = memberPage.map(member -> {
            Long roleId = member.getId();
            String roleName = "No Role";
            if (roleId != null) {
                Optional<Role> role = roleService.findById(roleId);
                roleName = role.get().getRole();
            }
            return new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), roleName);
        });
        return pagedMemberDTOWithoutPw;
    }

    public Page<Member> findMembers(String name, String email, String phone, String role, String content, Pageable pageable) {
        Page<Member> memberPage = memberRepository.findMembers(name, email, phone, role, content, pageable);
        return memberPage;
    }

    public String StringBuilder(String name, String email, String phone, String role, String content, Page<MemberDTOWithoutPw> pagedMemberDTOWithoutPw) {
        StringBuilder searchCriteria = new StringBuilder();
        if (name != null) searchCriteria.append(name).append(", ");
        if (email != null) searchCriteria.append(email).append(", ");
        if (phone != null) searchCriteria.append(phone).append(", ");
        if (role != null) searchCriteria.append(role).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        String successMessage;
        if (!searchCriteria.isEmpty()) {
            searchCriteria.setLength(searchCriteria.length() - 2);
            successMessage = "검색 키워드 : " + searchCriteria;
        } else {
            successMessage = "전체 회원입니다.";
        }
        long totalCount = pagedMemberDTOWithoutPw.getTotalElements();
        if (totalCount == 0) {
            successMessage = "검색 결과가 없습니다. 검색 키워드 : " + searchCriteria;
        }

        return successMessage;
    }

    public String verifyUser(String email, PasswordDTO password) {

        Long id = findByEmail(email).get().getId();
        if (!passwordEncoder.matches(password.getPassword(), findByEmail(email).get().getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String verificationToken = jwtTokenProvider.generateVerificationToken(email, id);

        return verificationToken;
    }

    public Optional<MemberDTOWithoutPw> update(MemberDTOWithPasswordConfirm memberDTOWithPasswordConfirm, String verificationToken) {
        String email = jwtTokenProvider.getEmailFromToken(verificationToken);

        if (email == null) {
            throw new JwtException("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요.");
        }
        if (!memberDTOWithPasswordConfirm.getPassword().equals(memberDTOWithPasswordConfirm.getPasswordConfirm())) {
            throw new IllegalStateException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Long id = Long.parseLong(jwtTokenProvider.getIdFromVerificationToken(verificationToken));

        Member member = new Member();
        member.setId(id);
        member.setName(memberDTOWithPasswordConfirm.getName());
        member.setPhone(memberDTOWithPasswordConfirm.getPhone());
        member.setEmail(memberDTOWithPasswordConfirm.getEmail());
        member.setPassword(memberDTOWithPasswordConfirm.getPassword());
        member.setRole(roleService.findById(id).get());

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
        memberRepository.save(existingMember);

        Optional<MemberDTOWithoutPw> memberDTOWithoutPw = Optional.of(existingMember).map(memberWithoutPw -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), member.getRole().getRole()));

        return memberDTOWithoutPw;

    }


    public void deleteById(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다."));
        memberRepository.deleteById(memberId);
    }

}
