package home.project.service;

//import home.project.domain.LoginDto;
import home.project.domain.Member;
import home.project.domain.MemberDTOWithoutId;
import home.project.domain.MemberDTOWithoutPw;
//import home.project.domain.TokenDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
//   ResponseEntity<?> login(LoginDto loginDto);

//   void login(LoginDto loginDto);
   void join(Member member);

   Optional<Member> findByEmail(String email);
   List<Member> findAll();
   Optional<Member> update(Member member);
   void deleteMember(String email);
}
