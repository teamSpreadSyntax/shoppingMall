package home.project.controller;

import home.project.domain.LoginDto;
import home.project.domain.TokenDto;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "로그인", description = "로그인 토큰관련 API 입니다")
@RequestMapping(path = "/api/loginToken")
@RestController
public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private MemberService memberService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, MemberService memberService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.memberService = memberService;
    }

    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }
        try {
//            return ResponseEntity.ok(loginDto);
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            String token = tokenProvider.createToken(authentication);
            TokenDto tokenDto = new TokenDto();
            tokenDto.setInfo(token);
//            memberService.login(loginDto);
            return ResponseEntity.ok(tokenDto);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}


