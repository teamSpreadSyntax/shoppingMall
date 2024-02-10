package home.project.service;

import home.project.domain.Member;

import java.util.Optional;

public interface MemberService {
    void join(Member member);
   void validateDuplicateMember(Optional<Member> name);

}
