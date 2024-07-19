package home.project.controller;

import home.project.domain.*;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.service.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "로그인, 로그아웃", description = "로그인, 로그아웃관련 API입니다")
@RequestMapping(path = "/api/loginToken")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = @Content(schema = @Schema(ref = "#/components/schemas/LoginValidationFailedResponseSchema"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
        @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema"))),
        @ApiResponse(responseCode = "409", description = "Conflict",
                content = @Content(schema = @Schema(ref = "#/components/schemas/ConflictResponseSchema"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final ValidationCheck validationCheck;
    private final RoleService roleService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, MemberService memberService, ValidationCheck validationCheck, RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.memberService = memberService;
        this.validationCheck = validationCheck;
        this.roleService = roleService;
    }

    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginSuccessResponseSchema")))
    })
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Valid UserDetailsDTO userDetailsDTO, BindingResult bindingResult) {
        CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;
        UserDetails member = userDetailsService.loadUserByUsername(userDetailsDTO.getEmail());
        if (!passwordEncoder.matches(userDetailsDTO.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호를 확인해주세요.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetailsDTO.getEmail(), userDetailsDTO.getPassword()));
        System.out.println(authentication);
        TokenDto tokenDto = tokenProvider.generateToken(authentication);
        String successMessage = member.getUsername() + "(으)로 로그인에 성공했습니다.";
        return new CustomOptionalResponseEntity<>(Optional.of(tokenDto), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "로그아웃 메서드", description = "로그아웃 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema")))
    })
    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestParam("memberId") Long memberId) {
        Optional<Member> member = memberService.findById(memberId);
        memberService.logout(memberId);
        Optional<Role> role = roleService.findById(memberId);
        String Authority = role.get().getRole();
        if (Authority.equals("ROLE_USER")){
            role.get().setRole("user");
        } else if (Authority.equals("ROLE_ADMIN")) {
            role.get().setRole("admin");
        } else if (Authority.equals("ROLE_CENTER")) {
            role.get().setRole("center");
        }
        roleService.update(role.get());
        String email = member.get().getEmail();
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email+"님 이용해주셔서 감사합니다.");
        return new CustomOptionalResponseEntity<>(Optional.of(responseMap), "로그아웃되었습니다.", HttpStatus.OK);

    }

    @Operation(summary = "권한 부여 메서드", description = "권한 부여 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/AuthorityChangeSuccessResponseSchema")))
    })
    @PostMapping("authority")
    @PreAuthorize("hasRole('ROLE_CENTER')")
    public ResponseEntity<?> addAuthority(@RequestParam("memberId") Long memberId, @RequestParam("authority") String authority) {
        String successMessage = "";
        try {
            Role role = roleService.findById(memberId).get();
                if (authority.equals("admin")) {
                    role.setRole("admin");
                    successMessage = memberId + "에게 관리자 권한을 부여했습니다.";
                } else if (authority.equals("center")) {
                    role.setRole("center");
                    successMessage = memberId + "에게 최고 관리자 권한을 부여했습니다.";
                } else if (authority.equals("user")) {
                    role.setRole("user");
                    successMessage = memberId + "에게 일반 사용자 권한을 부여했습니다.";
                }
            roleService.update(role);
            return new CustomOptionalResponseEntity<>(Optional.of(role), successMessage, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            String errorMessage = "접근 권한이 없습니다.";
            return new CustomOptionalResponseEntity<>(Optional.of(e.getMessage()), errorMessage, HttpStatus.FORBIDDEN);
        }
    }

}


