package home.project.service;

import home.project.domain.Member;
import home.project.domain.Role;
import home.project.domain.RoleType;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponseDTO;
import home.project.dto.responseDTO.TokenResponseDTO;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Member convertToEntity(CreateMemberRequestDTO memberDTOWithoutId) {
        Member member = new Member();
        member.setEmail(memberDTOWithoutId.getEmail());
        member.setPassword(passwordEncoder.encode(memberDTOWithoutId.getPassword()));
        member.setName(memberDTOWithoutId.getName());
        member.setPhone(memberDTOWithoutId.getPhone());
        return member;
    }

    @Override
    @Transactional
    public TokenResponseDTO join(CreateMemberRequestDTO createMemberRequestDTO) {

        if (!createMemberRequestDTO.getPassword().equals(createMemberRequestDTO.getPasswordConfirm())) {
            throw new IllegalStateException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        boolean emailExists = memberRepository.existsByEmail(createMemberRequestDTO.getEmail());
        boolean phoneExists = memberRepository.existsByPhone(createMemberRequestDTO.getPhone());
        if (emailExists && phoneExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일과 전화번호입니다.");
        } else if (emailExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 이메일입니다.");
        } else if (phoneExists) {
            throw new DataIntegrityViolationException("이미 사용 중인 전화번호입니다.");
        }

        Member member = convertToEntity(createMemberRequestDTO);
        memberRepository.save(member);
        Optional<Member> memberForAddRole = findByEmail(member.getEmail());
        Long id = memberForAddRole.get().getId();

        Role role = new Role();
        role.setId(id);
        roleService.join(role);

        RoleType savedRole = roleService.findById(id).get().getRole();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(createMemberRequestDTO.getEmail(), createMemberRequestDTO.getPassword()));
        TokenResponseDTO tokenResponseDTO = jwtTokenProvider.generateToken(authentication);

        tokenResponseDTO.setRole(savedRole);

        return tokenResponseDTO;
    }

    @Override
    public Optional<MemberResponseDTO> memberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long memberId = findByEmail(email).get().getId();
        Optional<Member> memberOptional = findById(memberId);
        RoleType role = roleService.findById(memberId).get().getRole();
        Optional<MemberResponseDTO> memberResponseDTO = memberOptional.map(member -> new MemberResponseDTO(member.getId(), member.getEmail(), member.getName(), member.getPhone(), role));
        return memberResponseDTO;
    }

    @Override
    public Optional<Member> findById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다.");
        });
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new IdNotFoundException(email + "(으)로 등록된 회원이 없습니다.");
        });
        return Optional.ofNullable(member);
    }

    @Override
    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Override
    public Page<MemberResponseDTO> convertToMemberDTOWithoutPW(Page<Member> memberPage) {
        Page<MemberResponseDTO> pagedMemberDTOWithoutPw = memberPage.map(member -> {
            Long roleId = member.getId();
            RoleType roleName = RoleType.valueOf("No Role");
            if (roleId != null) {
                Optional<Role> role = roleService.findById(roleId);
                roleName = role.get().getRole();
            }
            return new MemberResponseDTO(member.getId(), member.getEmail(), member.getName(), member.getPhone(), roleName);
        });
        return pagedMemberDTOWithoutPw;
    }

    @Override
    public Page<Member> findMembers(String name, String email, String phone, String role, String content, Pageable pageable) {
        Page<Member> memberPage = memberRepository.findMembers(name, email, phone, role, content, pageable);
        return memberPage;
    }

    @Override
    public String stringBuilder(String name, String email, String phone, String role, String content, Page<MemberResponseDTO> pagedMemberDTOWithoutPw) {
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

    @Override
    public String verifyUser(String email, VerifyUserRequestDTO password) {

        Long id = findByEmail(email).get().getId();
        if (!passwordEncoder.matches(password.getPassword(), findByEmail(email).get().getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String verificationToken = jwtTokenProvider.generateVerificationToken(email, id);

        return verificationToken;
    }

    @Override
    @Transactional
    public Optional<MemberResponseDTO> update(UpdateMemberRequestDTO updateMemberRequestDTO, String verificationToken) {
        String email = jwtTokenProvider.getEmailFromToken(verificationToken);

        if (email == null) {
            throw new JwtException("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요.");
        }
        if (!updateMemberRequestDTO.getPassword().equals(updateMemberRequestDTO.getPasswordConfirm())) {
            throw new IllegalStateException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Long id = Long.parseLong(jwtTokenProvider.getIdFromVerificationToken(verificationToken));

        Member member = new Member();
        member.setId(id);
        member.setName(updateMemberRequestDTO.getName());
        member.setPhone(updateMemberRequestDTO.getPhone());
        member.setEmail(updateMemberRequestDTO.getEmail());
        member.setPassword(updateMemberRequestDTO.getPassword());
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

        Optional<MemberResponseDTO> memberResponseDTO = Optional.of(existingMember).map(memberWithoutPw -> new MemberResponseDTO(member.getId(), member.getEmail(), member.getName(), member.getPhone(), member.getRole().getRole()));

        return memberResponseDTO;

    }

    @Override
    @Transactional
    public void deleteById(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다."));
        memberRepository.deleteById(memberId);
    }

}
