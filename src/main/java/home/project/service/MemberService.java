package home.project.service;

import home.project.domain.Member;

import java.util.List;
import java.util.Optional;


public interface MemberService {
   void login(Member member);
   void join(Member member);
   void validateDuplicateMember(Member member);
   void joinConfirm(Member member);
   Optional<Member> findById(Long memberId);
   List<Member> findAll();

}
