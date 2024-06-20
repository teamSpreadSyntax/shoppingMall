package home.project.service;

import home.project.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface MemberService {

   void join(Member member);

   void logout(Long id);

   Optional<Member> findById(Long id);

   Optional<Member> findByEmail(String email);

   Page<Member> findAll(Pageable pageable);

   Optional<Member> update(Member member);

   void deleteMember(Long memberId);

   Page<Member> findMembers(String name, String email, String phone,String query,Pageable pageable);

}
