package home.project.controller.admin;

import home.project.domain.member.RoleType;
import home.project.dto.requestDTO.LoginRequestDTO;
import home.project.dto.responseDTO.RoleResponse;
import home.project.dto.responseDTO.TokenResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.member.AuthService;
import home.project.service.util.PageUtil;
import home.project.service.validation.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "관리자 로그인, 로그아웃, 권한", description = "관리자 로그인, 로그아웃, 권한 관련 API입니다.")
@RequestMapping(path = "/api/admin/auth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AdminAuthController {

    private final ValidationCheck validationCheck;
    private final AuthService authService;
    private final PageUtil pageUtil;

    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/TokenResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO, BindingResult bindingResult) {
        CustomResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;

        TokenResponse TokenResponse = authService.login(loginRequestDTO);

        String successMessage = loginRequestDTO.getEmail() + "(으)로 로그인에 성공했습니다.";

        return new CustomResponseEntity<>(TokenResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "토큰 갱신 메서드", description = "만료된 액세스 토큰과 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/TokenResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema")))
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestParam(value = "refreshToken", required = true) String refreshToken) {

        TokenResponse newTokenDto = authService.refreshToken(refreshToken);

        return new CustomResponseEntity<>(newTokenDto, "토큰이 성공적으로 갱신되었습니다.", HttpStatus.OK);
    }


    @Operation(summary = "권한 부여 메서드", description = "권한 부여 메서드입니다. (center : 중앙 관리자, admin : 중간 관리자, user : 일반 사용자)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/AssignRoleResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @PostMapping("/authorization")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addAuthority(@RequestParam("memberId") Long memberId, @RequestParam("authority") RoleType authority) {

        authService.addAuthority(memberId, authority);
        String successMessage = authService.roleMessage(memberId, authority);

        return new CustomResponseEntity<>(authority, successMessage, HttpStatus.OK);

    }

    @Operation(summary = "전체 회원별 권한 조회 메서드", description = "전체 회원별 권한 조회 메서드입니다. (center : 중앙 관리자, admin : 중간 관리자, user : 일반 사용자)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedUserRoleListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
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
        Page<RoleResponse> rolesWithMemberNamesPage = authService.checkAuthority(pageable);
        long totalCount = rolesWithMemberNamesPage.getTotalElements();
        int page = rolesWithMemberNamesPage.getNumber();
        return new CustomResponseEntity<>(rolesWithMemberNamesPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "토큰 유효성 확인 메서드", description = "새로고침할때마다 액세스토큰과 리프레쉬토큰을 검증하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/TokenResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),

    })
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(
            @RequestParam(value = "accessToken", required = true) String accessToken,
            @RequestParam(value = "refreshToken", required = true) String refreshToken) {
        TokenResponse newTokenDto = authService.verifyUser(accessToken, refreshToken);
        return new CustomResponseEntity<>(newTokenDto, "토큰이 검증되었습니다.", HttpStatus.OK);
    }

}
