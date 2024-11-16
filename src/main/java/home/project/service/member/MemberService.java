package home.project.service.member;

import home.project.domain.member.Member;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.CreateSocialMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.MemberResponseForUser;
import home.project.dto.responseDTO.TokenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    TokenResponse join(CreateMemberRequestDTO member);

    TokenResponse socialJoin(CreateSocialMemberRequestDTO member);

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

    void sendPasswordResetEmail(String email);

    void resetPassword(String token, String newPassword);
}
