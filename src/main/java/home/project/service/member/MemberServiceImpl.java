package home.project.service.member;

import home.project.domain.elasticsearch.MemberDocument;
import home.project.domain.member.Member;
import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.CreateSocialMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.MemberResponseForUser;
import home.project.dto.responseDTO.TokenResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.member.MemberRepository;
import home.project.repositoryForElasticsearch.MemberElasticsearchRepository;
import home.project.service.util.Converter;
import home.project.service.common.EmailService;
import home.project.service.integration.IndexToElasticsearch;
import home.project.service.security.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final Converter converter;
    private final IndexToElasticsearch indexToElasticsearch;
    private final ElasticsearchOperations elasticsearchOperations;
    private final MemberElasticsearchRepository memberElasticsearchRepository;
    private final EmailService emailService;



    @Override
    @Transactional
    public TokenResponse join(CreateMemberRequestDTO createMemberRequestDTO) {

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

        Member member = converter.convertFromCreateMemberRequestDTOToMember(createMemberRequestDTO);
        memberRepository.save(member);

        MemberDocument memberDocument = converter.convertFromMemberToMemberDocument(member);
        try {
            indexToElasticsearch.indexDocumentToElasticsearch(memberDocument, MemberDocument.class);
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(createMemberRequestDTO.getEmail(), createMemberRequestDTO.getPassword()));
        TokenResponse TokenResponse = jwtTokenProvider.generateToken(authentication);

        RoleType savedRole = findById(member.getId()).getRole();
        TokenResponse.setRole(savedRole);

        return TokenResponse;
    }

    @Override
    @Transactional
    public TokenResponse socialJoin(CreateSocialMemberRequestDTO createSocialMemberRequestDTO) {

        CreateMemberRequestDTO createMemberRequestDTO = new CreateMemberRequestDTO();
        createMemberRequestDTO.setPassword("null");
        createMemberRequestDTO.setPasswordConfirm("null");
        createMemberRequestDTO.setEmail(createSocialMemberRequestDTO.getEmail());
        createMemberRequestDTO.setName(createSocialMemberRequestDTO.getName());
        createMemberRequestDTO.setPhone(createSocialMemberRequestDTO.getPhone());
        createMemberRequestDTO.setGender(createSocialMemberRequestDTO.getGender());
        createMemberRequestDTO.setBirthDate(createSocialMemberRequestDTO.getBirthDate());
        createMemberRequestDTO.setDefaultAddress(createSocialMemberRequestDTO.getDefaultAddress());

        return join(createMemberRequestDTO);




    }

    @Override
    public MemberResponse memberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long memberId = findByEmail(email).getId();
        Member member = findById(memberId);
        return converter.convertFromMemberToMemberResponse(member);
    }

    @Override
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IdNotFoundException(memberId + "(으)로 등록된 회원이 없습니다."));
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException(email + "(으)로 등록된 회원이 없습니다."));
    }

    @Override
    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Override
    public Page<MemberResponse> findAllReturnPagedMemberResponse(Pageable pageable){
        Page<Member> pagedMember = memberRepository.findAll(pageable);
        return converter.convertFromPagedMemberToPagedMemberResponse(pagedMember);
    }


    @Override
    public Page<MemberResponse> findMembers(String name, String email, String phone, String role, String content, Pageable pageable) {
        Page<Member> pagedMember = memberRepository.findMembers(name, email, phone, role, content, pageable);
        return converter.convertFromPagedMemberToPagedMemberResponse(pagedMember);
    }

    @Override
    public Page<MemberResponse> findMembersOnElasticForManaging(String name, String email, String phone, String role, String content, Pageable pageable) {
        Page<MemberDocument> pagedDocuments = memberElasticsearchRepository.findMembers(name, email, phone, role, content, pageable);
        Page<Member> pagedMember = pagedDocuments.map(memberDocument -> findById(memberDocument.getId()));
        return converter.convertFromPagedMemberToPagedMemberResponse(pagedMember);
    }

    @Override
    public String verifyUser(String email, VerifyUserRequestDTO password) {

        Long id = findByEmail(email).getId();

        if (!passwordEncoder.matches(password.getPassword(), findByEmail(email).getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.generateVerificationToken(email, id);
    }

    @Override
    @Transactional
    public MemberResponseForUser update(UpdateMemberRequestDTO updateMemberRequestDTO, String verificationToken) {
        String email = jwtTokenProvider.getEmailFromToken(verificationToken);
        if (email == null) {
            throw new JwtException("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요.");
        }
        if (!updateMemberRequestDTO.getPassword().equals(updateMemberRequestDTO.getPasswordConfirm())) {
            throw new IllegalStateException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Long id = Long.parseLong(jwtTokenProvider.getIdFromVerificationToken(verificationToken));
        Member existingMember = findById(id);

        boolean isModified = false;
        boolean isEmailDuplicate = false;
        boolean isPhoneDuplicate = false;

        if (updateMemberRequestDTO.getName() != null && !updateMemberRequestDTO.getName().equals(existingMember.getName())) {
            existingMember.setName(updateMemberRequestDTO.getName());
            isModified = true;
        }

        if (updateMemberRequestDTO.getEmail() != null && !updateMemberRequestDTO.getEmail().equals(existingMember.getEmail())) {
            if (memberRepository.existsByEmail(updateMemberRequestDTO.getEmail())) {
                isEmailDuplicate = true;
            } else {
                existingMember.setEmail(updateMemberRequestDTO.getEmail());
                isModified = true;
            }
        }

        if (updateMemberRequestDTO.getPhone() != null && !updateMemberRequestDTO.getPhone().equals(existingMember.getPhone())) {
            if (memberRepository.existsByPhone(updateMemberRequestDTO.getPhone())) {
                isPhoneDuplicate = true;
            } else {
                existingMember.setPhone(updateMemberRequestDTO.getPhone());
                isModified = true;
            }
        }

        if (updateMemberRequestDTO.getPassword() != null && !passwordEncoder.matches(updateMemberRequestDTO.getPassword(), existingMember.getPassword())) {
            existingMember.setPassword(passwordEncoder.encode(updateMemberRequestDTO.getPassword()));
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



        Member updatedMember = memberRepository.save(existingMember);

        MemberDocument memberDocument = converter.convertFromMemberToMemberDocument(existingMember);
        indexToElasticsearch.indexDocumentToElasticsearch(memberDocument, MemberDocument.class);

        return new MemberResponseForUser(
                updatedMember.getId(),
                updatedMember.getEmail(),
                updatedMember.getName(),
                updatedMember.getPhone(),
                updatedMember.getGender(),
                updatedMember.getBirthDate(),
                updatedMember.getDefaultAddress(),
                updatedMember.getSecondAddress(),
                updatedMember.getThirdAddress(),
                updatedMember.getGrade(),
                updatedMember.getPoint(),
                converter.convertFromListedMemberCouponMemberCouponResponse(updatedMember.getMemberCoupons())
        );
    }

    @Override
    @Transactional
    public String deleteById(Long memberId) {
        String email = findById(memberId).getEmail();
        memberRepository.deleteById(memberId);
        elasticsearchOperations.delete(String.valueOf(memberId), MemberDocument.class);

        return email;
    }

    @Override
    @Transactional
    public String cancelMember(Long memberId, String verificationToken) {
        String email = jwtTokenProvider.getEmailFromToken(verificationToken);
        if (email == null) {
            throw new JwtException("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요.");
        }
        memberRepository.deleteById(memberId);
        elasticsearchOperations.delete(String.valueOf(memberId), MemberDocument.class);
        return email;
    }

    @Override
    @Transactional
    public MemberResponse updatePoint(Long memberId, Long point){
        Member member = findById(memberId);
        Long newPoint = member.getPoint() + point;
        member.setPoint(newPoint);
        memberRepository.save(member);
        MemberDocument memberDocument = converter.convertFromMemberToMemberDocument(member);
        indexToElasticsearch.indexDocumentToElasticsearch(memberDocument, MemberDocument.class);
        return converter.convertFromMemberToMemberResponse(member);
    }

    @Override
    public String findEmail(String name, String phone) {
        Member member = memberRepository.findByNameAndPhone(name, phone)
                .orElseThrow(() -> new IdNotFoundException("일치하는 회원이 없습니다."));
        return member.getEmail();
    }

    @Override
    @Transactional
    public void sendPasswordResetEmail(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException("등록된 이메일이 없습니다."));

        String token = jwtTokenProvider.generateResetToken(email); // 토큰 생성
        String resetLink = "https://www.projectkkk.com/reset-password?token=" + token;

        emailService.sendEmail(email, "비밀번호 재설정 요청",
                "비밀번호 재설정을 원하시면 다음 링크를 클릭하세요: " + resetLink);

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IdNotFoundException("등록된 이메일이 없습니다."));

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

}
