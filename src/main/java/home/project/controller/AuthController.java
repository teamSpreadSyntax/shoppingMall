package home.project.controller;

import home.project.domain.Member;
import home.project.domain.TokenDto;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "로그인", description = "로그인 토큰관련 API 입니다")
@RequestMapping(path = "/api/loginToken")
@RestController
public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private  final MemberService memberService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserDetailsService userDetailsService,  PasswordEncoder passwordEncoder, MemberService memberService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.memberService = memberService;
    }

    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid home.project.domain.UserDetails userDetailss, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {//유효성 검사 단계
            // BindingResult : 요청 데이터를 바이딩할때 발생한 에러나 검증 실패정보를 저장하고 처리하는 역할
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }
        try {//로그인 로직 수행
             userDetailsService.loadUserByUsername(userDetailss.getEmail());
             Optional<Member> member = memberService.findByEmail(userDetailss.getEmail());
            if (!passwordEncoder.matches(userDetailss.getPassword(), member.get().getPassword())) {throw new DataIntegrityViolationException("비밀번호를 확인해주세요");}
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetailss.getEmail(), userDetailss.getPassword()));
            TokenDto tokenDto = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(tokenDto);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}


