package home.project.service;

import home.project.domain.Member;
import home.project.domain.RoleType;
import home.project.domain.elasticsearch.MemberDocument;
import home.project.dto.MemberEventDTO;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.MemberResponseForUser;
import home.project.dto.responseDTO.TokenResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.MemberRepository;
import home.project.repositoryForElasticsearch.MemberElasticsearchRepository;
import home.project.util.IndexToElasticsearch;
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

import java.time.LocalDate;
import java.time.Period;

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
    private final KafkaEventProducerService kafkaEventProducerService;




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

        kafkaEventProducerService.sendMemberJoinEvent(new MemberEventDTO("member-join", member.getGender(), Period.between(member.getBirthDate(), LocalDate.now()).getYears()));

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(createMemberRequestDTO.getEmail(), createMemberRequestDTO.getPassword()));
        TokenResponse TokenResponse = jwtTokenProvider.generateToken(authentication);

        RoleType savedRole = findById(member.getId()).getRole();
        TokenResponse.setRole(savedRole);

        return TokenResponse;
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

}
