package home.project.service;

import home.project.domain.Member;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.TokenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    TokenResponse join(CreateMemberRequestDTO member);

    MemberResponse memberInfo();

    Member findById(Long memberId);

    Member findByEmail(String email);

    Page<Member> findAll(Pageable pageable);

    Page<MemberResponse> findAllReturnPagedMemberResponse(Pageable pageable);


    Page<MemberResponse> findMembers(String name, String email, String phone, String role, String content, Pageable pageable);

    String verifyUser(String email, VerifyUserRequestDTO password);

    MemberResponse update(UpdateMemberRequestDTO updateMemberRequestDTO, String verificationToken);

    String deleteById(Long memberId);
}
