package home.project.service;

import home.project.domain.Member;
import home.project.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberService {

    Member convertToEntity(MemberDTOWithoutId memberDTOWithoutId);

    TokenDto join(MemberDTOWithoutId member);

    Optional<MemberDTOWithoutPw> memberInfo();

    Optional<Member> findById(Long memberId);

    Optional<Member> findByEmail(String email);

    Page<Member> findAll(Pageable pageable);

    Page<MemberDTOWithoutPw> convertToMemberDTOWithoutPW(Page<Member> memberPage);

    String StringBuilder(String name, String email, String phone, String role, String content, Page<MemberDTOWithoutPw> pagedMemberDTOWithoutPw);

    Page<Member> findMembers(String name, String email, String phone, String role, String content, Pageable pageable);

    String verifyUser(String email, PasswordDTO password);

    Optional<MemberDTOWithoutPw> update(MemberDTOWithPasswordConfirm memberDTOWithPasswordConfirm, String verificationToken);

    void deleteById(Long memberId);
}
