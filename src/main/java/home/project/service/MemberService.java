package home.project.service;

import home.project.domain.Member;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.MemberResponseForUser;
import home.project.dto.responseDTO.TokenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {

    TokenResponse join(CreateMemberRequestDTO member);

    MemberResponse memberInfo();

    Member findById(Long memberId);

    Member findByEmail(String email);

    Page<Member> findAll(Pageable pageable);

    Page<MemberResponse> findAllReturnPagedMemberResponse(Pageable pageable);


    Page<MemberResponse> findMembers(String name, String email, String phone, String role, String content, Pageable pageable);
    Page<MemberResponse> findMembersOnElasticForManaging(String name, String email, String phone, String role, String content, Pageable pageable);

    String verifyUser(String email, VerifyUserRequestDTO password);

    MemberResponseForUser update(UpdateMemberRequestDTO updateMemberRequestDTO, String verificationToken);

    String deleteById(Long memberId);

    String cancelMember(Long memberId, String verificationToken);

    MemberResponse updatePoint(Long memberId, Long point);

    String findEmail(String name, String phone);

    @Transactional
    void sendPasswordResetEmail(String email);

    @Transactional
    void resetPassword(String token, String newPassword);
}
