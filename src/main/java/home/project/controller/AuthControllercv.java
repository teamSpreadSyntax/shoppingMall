//package home.project.controller;
//
//import home.project.service.JwtTokenProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import jakarta.validation.Valid;
//
//@RestController
//public class AuthControllercv {
//
//    private AuthenticationManager authenticationManager;
//    private JwtTokenProvider tokenProvider;
//
//    @Autowired
//    public AuthControllercv(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
//        this.authenticationManager = authenticationManager;
//        this.tokenProvide = tokenProvide;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, Errors errors) {
//        if (errors.hasErrors()) {
//            // 에러 처리 로직 추가
//        }
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//        String token = tokenProvider.createToken(authentication);
//
//        return ResponseEntity.ok(new LoginResponse(token));
//    }
//}
