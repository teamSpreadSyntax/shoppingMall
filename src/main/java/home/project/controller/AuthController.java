package home.project.controller;

import home.project.domain.*;
import home.project.dto.RoleDTOWithMemberName;
import home.project.dto.TokenDto;
import home.project.dto.UserDetailsDTO;
import home.project.response.CustomResponseEntity;
import home.project.service.*;
import home.project.util.PageUtil;
import home.project.util.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "로그인, 로그아웃", description = "로그인, 로그아웃, 권한 관련 API입니다.")
@RequestMapping(path = "/api/auth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final ValidationCheck validationCheck;
    private final AuthService authService;
    private final PageUtil pageUtil;

    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserDetailsDTO userDetailsDTO, BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        TokenDto tokenDto = authService.login(userDetailsDTO);

        String successMessage = userDetailsDTO.getEmail() + "(으)로 로그인에 성공했습니다.";

        return new CustomResponseEntity<>(Optional.of(tokenDto), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "토큰 갱신 메서드", description = "만료된 액세스 토큰과 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginSuccessResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestParam(value = "refreshToken", required = true) String refreshToken) {

        TokenDto newTokenDto = authService.refreshToken(refreshToken);

        return new CustomResponseEntity<>(Optional.of(newTokenDto), "토큰이 성공적으로 갱신되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "로그아웃 메서드", description = "로그아웃 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("memberId") Long memberId) {

        String email = authService.logout(memberId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email + "님 이용해주셔서 감사합니다.");
        return new CustomResponseEntity<>(Optional.of(responseMap), "로그아웃되었습니다.", HttpStatus.OK);

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
    @PostMapping("/authorization")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addAuthority(@RequestParam("memberId") Long memberId, @RequestParam("authority") @Pattern(regexp = "^(user|admin|center)$", message = "Authority must be either 'user', 'admin', or 'center'") String authority) {

        Optional<Role> role = authService.addAuthority(memberId, authority);
        String successMessage = authService.RoleMessage(memberId, authority);

        return new CustomResponseEntity<>(Optional.of(role), successMessage, HttpStatus.OK);

    }

    @Operation(summary = "전체 회원별 권한 조회 메서드", description = "전체 회원별 권한 조회 메서드입니다. (center : 중앙 관리자, admin : 중간 관리자, user : 일반 사용자)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedUserRoleListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
    })
    @GetMapping("/authorities")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> checkAuthority(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = pageUtil.pageable(pageable);
        String successMessage = "전체 회원별 권한 목록입니다.";
        Page<RoleDTOWithMemberName> rolesWithMemberNamesPage = authService.checkAuthority(pageable);
        long totalCount = rolesWithMemberNamesPage.getTotalElements();
        int page = rolesWithMemberNamesPage.getNumber();
        return new CustomResponseEntity<>(rolesWithMemberNamesPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "토큰 유효성 확인 메서드", description = "새로고침할때마다 액세스토큰과 리프레쉬토큰을 검증하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/LoginSuccessResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))
    })
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(
            @RequestParam(value = "accessToken", required = true) String accessToken,
            @RequestParam(value = "refreshToken", required = true) String refreshToken) {
        TokenDto newTokenDto = authService.verifyUser(accessToken, refreshToken);
        return new CustomResponseEntity<>(Optional.of(newTokenDto), "토큰이 검증되었습니다.", HttpStatus.OK);
    }

}
