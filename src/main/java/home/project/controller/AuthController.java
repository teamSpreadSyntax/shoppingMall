package home.project.controller;

import home.project.domain.*;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
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
    private final MemberService memberService;

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
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(errorMap), "Validation failed", HttpStatus.BAD_REQUEST.value());
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
        try {
             userDetailsService.loadUserByUsername(userDetailss.getEmail());
             Optional<Member> member = memberService.findByEmail(userDetailss.getEmail());
            if (!passwordEncoder.matches(userDetailss.getPassword(), member.get().getPassword())) {throw new BadCredentialsException("비밀번호를 확인해주세요");}
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetailss.getEmail(), userDetailss.getPassword()));
            TokenDto tokenDto = tokenProvider.generateToken(authentication);
            String successMessage = member.get().getEmail() + "로 로그인에 성공하였습니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(tokenDto), successMessage, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(e.getMessage()), "아이디가 없습니다", HttpStatus.UNAUTHORIZED.value());
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "로그아웃 메서드", description = "로그아웃 메서드입니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("memberId") Long id) {
            Optional<Member> member = memberService.findById(id);
            memberService.logout(id);
            String successMessage = member.get().getEmail() + "님 로그아웃에 성공하였습니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(member.get().getEmail()), successMessage, HttpStatus.OK);
    }

}


