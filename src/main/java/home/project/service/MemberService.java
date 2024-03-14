package home.project.service;

import home.project.domain.LoginDto;
import home.project.domain.Member;
import home.project.domain.Product;
import home.project.domain.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface MemberService {
//   ResponseEntity<?> login(LoginDto loginDto);
   void join(Member member);

   Optional<Member> findByEmail(String email);
   List<Member> findAll();
   void update(Member member);
   void deleteMember(Member member);
}
