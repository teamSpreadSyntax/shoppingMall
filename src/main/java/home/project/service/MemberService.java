package home.project.service;

import home.project.domain.Member;
import home.project.dto.requestDTO.CreateMemberRequestDTO;
import home.project.dto.requestDTO.UpdateMemberRequestDTO;
import home.project.dto.requestDTO.VerifyUserRequestDTO;
import home.project.dto.responseDTO.MemberResponse;
import home.project.dto.responseDTO.TokenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberService {

    Member convertToEntity(CreateMemberRequestDTO memberDTOWithoutId);

    TokenResponse join(CreateMemberRequestDTO member);

    Optional<MemberResponse> memberInfo();

    Optional<Member> findById(Long memberId);

    Optional<Member> findByEmail(String email);

    Page<Member> findAll(Pageable pageable);

    Page<MemberResponse> convertToMemberDTOWithoutPW(Page<Member> memberPage);

    String stringBuilder(String name, String email, String phone, String role, String content, Page<MemberResponse> pagedMemberDTOWithoutPw);

    Page<Member> findMembers(String name, String email, String phone, String role, String content, Pageable pageable);

    String verifyUser(String email, VerifyUserRequestDTO password);

    Optional<MemberResponse> update(UpdateMemberRequestDTO updateMemberRequestDTO, String verificationToken);

    void deleteById(Long memberId);
}
