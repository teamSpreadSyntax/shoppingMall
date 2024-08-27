package home.project.controller;

import home.project.domain.*;
import home.project.dto.RoleDTOWithMemberName;
import home.project.dto.TokenDto;
import home.project.dto.UserDetailsDTO;
import home.project.response.CustomListResponseEntity;
import home.project.response.CustomOptionalResponseEntity;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.util.ValidationCheck;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "로그인, 로그아웃", description = "로그인, 로그아웃, 권한 관련 API입니다.")
@RequestMapping(path = "/api/auth")
@ApiResponses(value = {
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
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
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
        TokenDto tokenDto = tokenProvider.generateToken(authentication);

//      String role = member.getAuthorities().toString();
//      tokenDto.setRole(role);
        Long id = memberService.findByEmail(userDetailsDTO.getEmail()).get().getId();
        String role = roleService.findById(id).get().getRole();
        tokenDto.setRole(role);
        String successMessage = member.getUsername() + "(으)로 로그인에 성공했습니다.";
        return new CustomOptionalResponseEntity<>(Optional.of(tokenDto), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "토큰 갱신 메서드", description = "만료된 액세스 토큰과 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/loginSuccessResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Not Found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/notFoundResponseSchema")))
    })
    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(
            @RequestParam(value = "refreshToken", required = true) String refreshToken) {

            TokenDto newTokenDto = tokenProvider.refreshAccessToken(refreshToken);

            String email = tokenProvider.getEmailFromAccessToken(newTokenDto.getAccessToken());
            Optional<Member> member = memberService.findByEmail(email);

            Long id = member.get().getId();
            Optional<Role> roleOptional = roleService.findById(id);

            String role = roleOptional.get().getRole();
            newTokenDto.setRole(role);

            return new CustomOptionalResponseEntity<>(Optional.of(newTokenDto), "토큰이 성공적으로 갱신되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "로그아웃 메서드", description = "로그아웃 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
            content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestParam("memberId") Long memberId) {
        Optional<Member> member = memberService.findById(memberId);
        memberService.logout(memberId);
        Optional<Role> role = roleService.findById(memberId);
        roleService.update(role.get());
        String email = member.get().getEmail();
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email+"님 이용해주셔서 감사합니다.");
        return new CustomOptionalResponseEntity<>(Optional.of(responseMap), "로그아웃되었습니다.", HttpStatus.OK);

    }

    @Operation(summary = "권한 부여 메서드", description = "권한 부여 메서드입니다. (center : 중앙 관리자, admin : 중간 관리자, user : 일반 사용자)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/AuthorityChangeSuccessResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("authorization")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_CENTER')")
    public ResponseEntity<?> addAuthority(@RequestParam("memberId") Long memberId, @RequestParam("authority") @Pattern(regexp = "^(user|admin|center)$", message = "Authority must be either 'user', 'admin', or 'center'") String authority) {

        String successMessage = "";


            Role role = roleService.findById(memberId).get();
            String name = memberService.findById(memberId).get().getName();
                if (authority.equals("admin")) {
                    role.setRole("admin");
                    successMessage = name + "(id : " + memberId + ")" + "님에게 중간 관리자 권한을 부여했습니다.";
                } else if (authority.equals("center")) {
                    role.setRole("center");
                    successMessage = name + "(id : " + memberId + ")" + "님에게 중앙 관리자 권한을 부여했습니다.";
                } else if (authority.equals("user")) {
                    role.setRole("user");
                    successMessage = name + "(id : " + memberId + ")" + "님에게 일반 사용자 권한을 부여했습니다.";
                }
            roleService.update(role);
            return new CustomOptionalResponseEntity<>(Optional.of(role), successMessage, HttpStatus.OK);

    }

    @Operation(summary = "전체 회원별 권한 조회 메서드", description = "전체 회원별 권한 조회 메서드입니다. (center : 중앙 관리자, admin : 중간 관리자, user : 일반 사용자)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedUserRoleListResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("authorities")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_CENTER')")
    public ResponseEntity<?> checkAuthority(
        @PageableDefault(page = 1, size = 5)
        @SortDefault.SortDefaults({
                @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        String successMessage = "전체 회원별 권한 목록입니다.";
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
            Page<Member> memberPage = memberService.findAll(pageable);
            Page<RoleDTOWithMemberName> rolesWithMemberNamesPage = memberPage.map(member -> {
                        String role = roleService.findById(member.getId()).get().getRole();
                        return new RoleDTOWithMemberName(member.getId(), role, member.getName());});
            long totalCount = memberPage.getTotalElements();
            int page = memberPage.getNumber();
            return new CustomListResponseEntity<>(rolesWithMemberNamesPage .getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "토큰 유효성 확인 메서드", description = "새로고침할때마다 액세스토큰과 리프레쉬토큰을 검증하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/TokenRefreshSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))
    })
    @GetMapping("verify")
    public ResponseEntity<?> verifyUser(
            @RequestParam(value = "accessToken", required = true) String accessToken,
            @RequestParam(value = "refreshToken", required = true)  String refreshToken) {
        tokenProvider.validateTokenResult(accessToken,refreshToken);
            String email = tokenProvider.getEmailFromAccessToken(accessToken);
            Optional<Member> member = memberService.findByEmail(email);
            if (!member.isPresent()) {
                throw new RuntimeException(email + "해당하는 회원이 존재하지 않습니다.");
            }
            Long id = member.get().getId();
            Optional<Role> roleOptional = roleService.findById(id);
            if (!roleOptional.isPresent()) {
                throw new RuntimeException("사용자 권한을 찾을 수 없습니다.");
            }
            String role = roleOptional.get().getRole();
            TokenDto newTokenDto = new TokenDto();
            newTokenDto.setRole(role);
            newTokenDto.setAccessToken(accessToken);
            newTokenDto.setRefreshToken(refreshToken);
            newTokenDto.setGrantType("Bearer");
            return new CustomOptionalResponseEntity<>(Optional.of(newTokenDto), "토큰이 검증 되었습니다.", HttpStatus.OK);
    }

}
