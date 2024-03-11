package home.project.service;

import home.project.domain.Member;
import home.project.domain.Product;

import java.util.List;
import java.util.Optional;


public interface MemberService {
   void login(Member member);
   void join(Member member);
   Optional<Member> findByEmail(String email);
   List<Member> findAll();
   void update(Member member);
   void deleteMember(Member member);
}
